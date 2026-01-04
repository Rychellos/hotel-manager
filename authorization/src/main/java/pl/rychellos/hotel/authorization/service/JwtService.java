package pl.rychellos.hotel.authorization.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {
    private final ApplicationExceptionFactory applicationExceptionFactory;
    private final LangUtil langUtil;
    @Value("${application.security.jwt.secret-key:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${application.security.jwt.expiration-ms:3600000}") // 1 hour
    private long jwtExpiration;

    public JwtService(ApplicationExceptionFactory applicationExceptionFactory, LangUtil langUtil) {
        this.applicationExceptionFactory = applicationExceptionFactory;
        this.langUtil = langUtil;
    }

    public String extractUsername(String token) throws ApplicationException {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws ApplicationException {
        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    public String generateToken(UserEntity userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserEntity userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserEntity userDetails, long expiration) {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact();
    }

    public boolean isTokenValid(String token, UserEntity userDetails) throws ApplicationException {
        String username = extractUsername(token);

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) throws ApplicationException {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws ApplicationException {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) throws ApplicationException {
        try {
            return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (JwtException exception) {
            if (exception instanceof ExpiredJwtException) {
                throw applicationExceptionFactory.forbidden(
                    langUtil.getMessage("error.token.access.expired")
                );
            }

            if (exception instanceof UnsupportedJwtException) {
                throw applicationExceptionFactory.forbidden(
                    langUtil.getMessage("error.token.access.malformed")
                );
            }
        } catch (Exception exception) {
            log.error("Unhandled exception type: {}\nToken: {}", exception.getClass().getSimpleName(), token, exception);
        }
        
        throw applicationExceptionFactory.forbidden(
            langUtil.getMessage("error.token.unknown")
        );
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
