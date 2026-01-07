package pl.rychellos.hotel.authorization.service;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.authorization.permission.PermissionEntity;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.authorization.service.dto.AuthRequestDTO;
import pl.rychellos.hotel.authorization.service.dto.AuthResponseDTO;
import pl.rychellos.hotel.authorization.service.dto.AuthResultDTO;
import pl.rychellos.hotel.authorization.token.RefreshTokenEntity;
import pl.rychellos.hotel.authorization.token.RefreshTokenService;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationExceptionFactory exceptionFactory;

    public AuthService(
        UserRepository userRepository,
        JwtService jwtService,
        AuthenticationManager authenticationManager,
        RefreshTokenService refreshTokenService,
        ApplicationExceptionFactory exceptionFactory
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.exceptionFactory = exceptionFactory;
    }

    public AuthResultDTO authenticate(AuthRequestDTO request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
            )
        );

        UserEntity user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> exceptionFactory.resourceNotFound("User not found"));

        String jwtToken = jwtService.generateToken(user);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user);

        AuthResponseDTO authResponseDTO = buildAuthResponse(user, jwtToken);

        return new AuthResultDTO(authResponseDTO, refreshToken.getTokenHash(), user.getUsername());
    }

    private AuthResponseDTO buildAuthResponse(UserEntity user, String jwtToken) {
        Set<String> roles = user.getRoles().stream()
            .map(RoleEntity::getName)
            .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(PermissionEntity::getName)
            .collect(Collectors.toSet());

        return new AuthResponseDTO(jwtToken, roles, permissions);
    }

    public AuthResultDTO refreshToken(String token) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.findByToken(token);
        refreshTokenService.verifyExpiration(refreshTokenEntity);

        // Track usage
        refreshTokenEntity.setAccessTokenCount(refreshTokenEntity.getAccessTokenCount() + 1);
        refreshTokenService.save(refreshTokenEntity);

        UserEntity user = refreshTokenEntity.getUser();
        String accessToken = jwtService.generateToken(user);

        AuthResponseDTO authResponseDTO = buildAuthResponse(user, accessToken);

        return new AuthResultDTO(authResponseDTO, token, user.getUsername());
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(Duration.ofDays(7))
            .build();
    }

    public ResponseCookie createLogoutCookie() {
        return ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .build();
    }
}
