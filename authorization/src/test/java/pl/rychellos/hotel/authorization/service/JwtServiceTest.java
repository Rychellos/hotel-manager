package pl.rychellos.hotel.authorization.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
class JwtServiceTest {

    @Mock
    private ApplicationExceptionFactory exceptionFactory;
    @Mock
    private LangUtil langUtil;

    @InjectMocks
    private JwtService jwtService;

    private final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private UserEntity user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);

        user = new UserEntity();
        user.setUsername("testuser");
    }

    @Test
    void generateToken_ShouldCreateValidJwt() throws ApplicationException {
        // When
        String token = jwtService.generateToken(user);

        // Then
        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForCorrectUser() throws ApplicationException {
        // Given
        String token = jwtService.generateToken(user);

        // When
        boolean isValid = jwtService.isTokenValid(token, user);

        // Then
        assertTrue(isValid);
    }

    @Test
    void extractAllClaims_ShouldThrowForbidden_WhenTokenIsMalformed() {
        // Given
        String malformedToken = "not.a.real.token";
        when(langUtil.getMessage(anyString())).thenReturn("Malformed");
        when(exceptionFactory.forbidden(anyString())).thenReturn(new ApplicationException("Title", "Detail", null));

        // When & Then
        assertThrows(ApplicationException.class, () -> jwtService.extractUsername(malformedToken));
    }
}
