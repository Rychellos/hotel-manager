package pl.rychellos.hotel.webapi;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rychellos.hotel.authorization.service.AuthService;
import pl.rychellos.hotel.authorization.service.dto.AuthRequestDTO;
import pl.rychellos.hotel.authorization.service.dto.AuthResponseDTO;
import pl.rychellos.hotel.authorization.service.dto.AuthResultDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.webapi.configuration.SwaggerConfiguration;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authorization", description = "Endpoints for user authorization")
public class AuthorizationController {
    private final AuthService authService;

    public AuthorizationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(
        value = "/login",
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE
        }
    )
    @SwaggerConfiguration.ApiProblemResponses(value = {
        HttpStatus.UNAUTHORIZED,
        HttpStatus.INTERNAL_SERVER_ERROR
    })
    public ResponseEntity<AuthResponseDTO> login(
        @ModelAttribute @RequestBody(required = false) AuthRequestDTO request,
        HttpServletResponse response
    ) throws ApplicationException {
        log.info("Login attempt for user with username {}", request.username());

        AuthResultDTO result = authService.authenticate(request);
        setRefreshTokenCookie(response, result.refreshToken());

        return ResponseEntity.ok(result.authResponseDTO());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(
        @CookieValue(name = "refresh_token") String refreshToken,
        HttpServletResponse response
    ) {
        AuthResultDTO result = authService.refreshToken(refreshToken);
        setRefreshTokenCookie(response, result.refreshToken());

        log.info("User with username {} refreshed token", result.username());

        return ResponseEntity.ok(result.authResponseDTO());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refresh_token") String refreshToken,
                                       HttpServletResponse response) {
        authService.logout(refreshToken);
        deleteRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
