package pl.rychellos.hotel.authorization.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.http.ResponseCookie;
import pl.rychellos.hotel.authorization.service.dto.AuthRequestDTO;
import pl.rychellos.hotel.authorization.service.dto.AuthResultDTO;
import pl.rychellos.hotel.authorization.token.RefreshTokenEntity;
import pl.rychellos.hotel.authorization.token.RefreshTokenService;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.authorization.user.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_ShouldReturnAuthResult_WhenCredentialsCorrect() {
        // Given
        AuthRequestDTO request = new AuthRequestDTO("user", "pass");
        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setRoles(Set.of());

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setTokenHash("dummy-refresh-hash");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        // When
        AuthResultDTO result = authService.authenticate(request);

        // Then
        assertNotNull(result);
        assertEquals("access-token", result.getAuthResponseDTO().getAccessToken());
        assertEquals("dummy-refresh-hash", result.getRefreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void refreshToken_ShouldRotateAndReturnNewResult() {
        // Given
        String oldRefreshToken = "old-hash";
        UserEntity user = new UserEntity();
        user.setRoles(Set.of());

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUser(user);
        entity.setAccessTokenCount(0L);

        when(refreshTokenService.findByToken(oldRefreshToken)).thenReturn(entity);
        when(jwtService.generateToken(user)).thenReturn("new-access-token");

        // When
        AuthResultDTO result = authService.refreshToken(oldRefreshToken);

        // Then
        assertEquals("new-access-token", result.getAuthResponseDTO().getAccessToken());
        assertEquals(1L, entity.getAccessTokenCount());
        verify(refreshTokenService).save(entity);
    }

    @Test
    void logout_ShouldInvokeRevocation() {
        // When
        authService.logout("token-to-revoke");

        // Then
        verify(refreshTokenService).revokeToken("token-to-revoke");
    }

    @Test
    void createRefreshTokenCookie_ShouldCreateCorrectCookie() {
        // When
        ResponseCookie cookie = authService.createRefreshTokenCookie("token-value");

        // Then
        assertNotNull(cookie);
        assertEquals("refresh_token", cookie.getName());
        assertEquals("token-value", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/api/", cookie.getPath());
        assertEquals(7 * 24 * 60 * 60, cookie.getMaxAge().getSeconds());
    }

    @Test
    void createLogoutCookie_ShouldCreateExpiredCookie() {
        // When
        ResponseCookie cookie = authService.createLogoutCookie();

        // Then
        assertNotNull(cookie);
        assertEquals("refresh_token", cookie.getName());
        assertTrue(cookie.getValue() == null || cookie.getValue().isEmpty());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/api/", cookie.getPath());
        assertEquals(0, cookie.getMaxAge().getSeconds());
    }
}