package pl.rychellos.hotel.webapi;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
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

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SwaggerConfiguration.ApiProblemResponses(value = {
        HttpStatus.UNAUTHORIZED,
        HttpStatus.INTERNAL_SERVER_ERROR
    })
    public ResponseEntity<AuthResponseDTO> loginJson(
        @RequestBody AuthRequestDTO request
    ) throws ApplicationException {
        return processLogin(request);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @SwaggerConfiguration.ApiProblemResponses(value = {
        HttpStatus.UNAUTHORIZED,
        HttpStatus.INTERNAL_SERVER_ERROR
    })
    public ResponseEntity<AuthResponseDTO> loginForm(
        @ModelAttribute AuthRequestDTO request
    ) throws ApplicationException {
        return processLogin(request);
    }

    private ResponseEntity<AuthResponseDTO> processLogin(
        AuthRequestDTO request
    ) throws ApplicationException {
        log.info("Login attempt for user with username {}", request.username());

        AuthResultDTO result = authService.authenticate(request);
        ResponseCookie cookie = authService.createRefreshTokenCookie(result.refreshToken());

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(result.authResponseDTO());
    }

    @PostMapping("/refresh")
    @Tag(name = "", description = "Refreshes token located inside cookie and returns new access token.")
    public ResponseEntity<AuthResponseDTO> refresh(
        @CookieValue(name = "refresh_token") String refreshToken
    ) {
        AuthResultDTO result = authService.refreshToken(refreshToken);
        ResponseCookie cookie = authService.createRefreshTokenCookie(result.refreshToken());

        log.info("User with username {} refreshed token", result.username());

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(result.authResponseDTO());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @CookieValue(name = "refresh_token") String refreshToken
    ) {
        authService.logout(refreshToken);
        ResponseCookie cookie = authService.createLogoutCookie();

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build();
    }
}
