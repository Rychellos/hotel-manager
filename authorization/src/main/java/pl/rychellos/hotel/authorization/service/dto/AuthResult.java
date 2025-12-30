package pl.rychellos.hotel.authorization.service.dto;

public record AuthResult(AuthResponse authResponse, String refreshToken) {
}