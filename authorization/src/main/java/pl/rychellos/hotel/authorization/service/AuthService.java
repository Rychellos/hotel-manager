package pl.rychellos.hotel.authorization.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.authorization.permission.PermissionEntity;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.authorization.service.dto.AuthRequest;
import pl.rychellos.hotel.authorization.service.dto.AuthResponse;
import pl.rychellos.hotel.authorization.service.dto.AuthResult;
import pl.rychellos.hotel.authorization.token.RefreshTokenEntity;
import pl.rychellos.hotel.authorization.token.RefreshTokenService;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationExceptionFactory exceptionFactory;

    public AuthService(UserRepository userRepository, JwtService jwtService,
                       AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService,
                       ApplicationExceptionFactory exceptionFactory) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.exceptionFactory = exceptionFactory;
    }

    public AuthResult authenticate(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserEntity user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> exceptionFactory.resourceNotFound("User not found"));

        String jwtToken = jwtService.generateToken(user);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user);

        AuthResponse authResponse = buildAuthResponse(user, jwtToken);

        return new AuthResult(authResponse, refreshToken.getTokenHash());
    }

    private AuthResponse buildAuthResponse(UserEntity user, String jwtToken) {
        Set<String> roles = user.getRoles().stream()
            .map(RoleEntity::getName)
            .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(PermissionEntity::getName)
            .collect(Collectors.toSet());

        return AuthResponse.builder()
            .accessToken(jwtToken)
            .roles(roles)
            .permissions(permissions)
            .build();
    }

    public AuthResult refreshToken(String token) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.findByToken(token);
        refreshTokenService.verifyExpiration(refreshTokenEntity);

        // Track usage
        refreshTokenEntity.setAccessTokenCount(refreshTokenEntity.getAccessTokenCount() + 1);
        refreshTokenService.save(refreshTokenEntity);

        UserEntity user = refreshTokenEntity.getUser();
        String accessToken = jwtService.generateToken(user);

        AuthResponse authResponse = buildAuthResponse(user, accessToken);


        return new AuthResult(authResponse, token);
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

}
