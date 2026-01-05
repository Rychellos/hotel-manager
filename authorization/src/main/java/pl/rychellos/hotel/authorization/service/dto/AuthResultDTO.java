package pl.rychellos.hotel.authorization.service.dto;

public record AuthResultDTO(
    AuthResponseDTO authResponseDTO,
    String refreshToken,
    String username
) {
}