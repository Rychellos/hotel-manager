package pl.rychellos.hotel.authorization.token;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private ApplicationExceptionFactory exceptionFactory;

    @Mock
    private LangUtil langUtil;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        // Set the @Value field manually for the test
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 86400000L);
    }

    @Test
    void createRefreshToken_ShouldSaveAndReturnToken() throws Exception {
        /// Given
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(refreshTokenRepository.save(any(RefreshTokenEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        /// When
        RefreshTokenEntity result = refreshTokenService.createRefreshToken(user);

        /// Then
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNotNull(result.getTokenHash());
        assertFalse(result.isRevoked());
        assertTrue(result.getExpiryDate().isAfter(Instant.now()));
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void verifyExpiration_ShouldThrowException_WhenTokenIsExpired() {
        /// Given
        RefreshTokenEntity expiredToken = new RefreshTokenEntity();
        expiredToken.setExpiryDate(Instant.now().minusSeconds(60)); // 1 minute ago

        when(langUtil.getMessage("error.token.refresh.expired")).thenReturn("Token expired");
        when(exceptionFactory.forbidden("Token expired"))
                .thenReturn(new ApplicationException("Forbidden", "Token expired", null));

        /// When & Then
        assertThrows(ApplicationException.class, () -> refreshTokenService.verifyExpiration(expiredToken));
        verify(refreshTokenRepository).delete(expiredToken);
    }

    @Test
    void verifyExpiration_ShouldReturnToken_WhenNotExpired() throws Exception {
        /// Given
        RefreshTokenEntity validToken = new RefreshTokenEntity();
        validToken.setExpiryDate(Instant.now().plusSeconds(60));

        /// When
        RefreshTokenEntity result = refreshTokenService.verifyExpiration(validToken);

        /// Then
        assertEquals(validToken, result);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void revokeToken_ShouldSetRevokedTrue_WhenFound() throws Exception {
        /// Given
        String hash = "some-hash";
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setRevoked(false);
        when(refreshTokenRepository.findByTokenHash(hash)).thenReturn(Optional.of(token));

        /// When
        refreshTokenService.revokeToken(hash);

        /// Then
        assertTrue(token.isRevoked());
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void findByToken_ShouldThrowNotFound_WhenMissing() {
        /// Given
        String hash = "missing-hash";
        when(refreshTokenRepository.findByTokenHash(hash)).thenReturn(Optional.empty());
        when(exceptionFactory.resourceNotFound(anyString()))
                .thenReturn(new ApplicationException("Not Found", "Missing", null));

        /// When & Then
        assertThrows(ApplicationException.class, () -> refreshTokenService.findByToken(hash));
    }
}
