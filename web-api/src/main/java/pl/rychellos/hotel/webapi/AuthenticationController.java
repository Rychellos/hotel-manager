package pl.rychellos.hotel.webapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pl.rychellos.hotel.authorization.service.AuthService;
import pl.rychellos.hotel.authorization.service.dto.AuthRequestDTO;
import pl.rychellos.hotel.authorization.service.dto.AuthResponseDTO;
import pl.rychellos.hotel.authorization.service.dto.AuthResponseOAuth2DTO;
import pl.rychellos.hotel.authorization.service.dto.AuthResultDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.webapi.configuration.SwaggerConfiguration;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthenticationController {
    private final AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SwaggerConfiguration.ApiProblemResponses(value = {
        HttpStatus.UNAUTHORIZED,
        HttpStatus.INTERNAL_SERVER_ERROR
    })
    @Operation(
        summary = "Used for user login using username and password.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = {
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthRequestDTO.class)),
                @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE, schema = @Schema(implementation = AuthRequestDTO.class))
            }
        )
    )
    public ResponseEntity<AuthResponseDTO> loginJson(
        @RequestBody AuthRequestDTO request
    ) throws ApplicationException {
        log.info("Login attempt using \"application/json\" for user with username {}", request.username());

        AuthResultDTO result = authService.authenticate(request);
        ResponseCookie cookie = authService.createRefreshTokenCookie(result.refreshToken());

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(result.authResponseDTO());
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @SwaggerConfiguration.ApiProblemResponses(value = {
        HttpStatus.UNAUTHORIZED,
        HttpStatus.INTERNAL_SERVER_ERROR
    })
    @Operation(
        summary = "Used for user login using username and password.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = {
                @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthRequestDTO.class)),
                @Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE, schema = @Schema(implementation = AuthRequestDTO.class))
            }
        )
    )
    public ResponseEntity<AuthResponseOAuth2DTO> loginForm(
        @ModelAttribute AuthRequestDTO request
    ) throws ApplicationException {
        log.info("Login attempt using \"application/x-www-form-urlencoded\" for user with username {}", request.username());

        AuthResultDTO result = authService.authenticate(request);
        ResponseCookie cookie = authService.createRefreshTokenCookie(result.refreshToken());

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new AuthResponseOAuth2DTO(
                result.authResponseDTO().accessToken(),
                result.refreshToken(),
                "Bearer"
            ));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refreshes token located inside cookie and returns new login response.")
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
    @Operation(summary = "Deletes token located inside cookie and revokes it in database.")
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
