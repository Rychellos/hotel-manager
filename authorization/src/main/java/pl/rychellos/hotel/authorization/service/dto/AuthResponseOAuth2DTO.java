package pl.rychellos.hotel.authorization.service.dto;

public record AuthResponseOAuth2DTO(
    String access_token,
    String refresh_token,
    String token_type
) {
}
