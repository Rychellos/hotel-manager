package pl.rychellos.hotel.authorization.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final LangUtil langUtil;
    @Value("${application.security.jwt.refresh-token.expiration-ms:86400000}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final ApplicationExceptionFactory applicationExceptionFactory;

    public RefreshTokenService(
        RefreshTokenRepository refreshTokenRepository,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.applicationExceptionFactory = applicationExceptionFactory;
        this.langUtil = langUtil;
    }

    public RefreshTokenEntity createRefreshToken(UserEntity user) {
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setTokenHash(UUID.randomUUID().toString()); // In real implementation, hash this!
        refreshToken.setRevoked(false);

        try {
            return refreshTokenRepository.save(refreshToken);
        } catch (Exception e) {
            throw applicationExceptionFactory.internalServerError(
                langUtil.getMessage("error.token.refresh.creation")
            );
        }
    }

    public void save(RefreshTokenEntity token) {
        refreshTokenRepository.save(token);
    }

    public RefreshTokenEntity verifyExpiration(
        RefreshTokenEntity token
    ) throws ApplicationException {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);

            throw applicationExceptionFactory.forbidden(
                langUtil.getMessage("error.token.refresh.expired")
            );
        }

        return token;
    }

    @Transactional
    public void revokeToken(String tokenHash) {
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        return 1;
    }

    @Scheduled(cron = "0 0 0 * * *") // Daily cleanup
    @Transactional
    public void purgeExpiredTokens() {
        refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }

    public RefreshTokenEntity findByToken(String token) {
        return refreshTokenRepository.findByTokenHash(token)
            .orElseThrow(() -> applicationExceptionFactory.resourceNotFound("Refresh token is not in database!"));
    }
}
