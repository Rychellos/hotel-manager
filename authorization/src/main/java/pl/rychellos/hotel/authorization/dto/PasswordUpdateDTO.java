package pl.rychellos.hotel.authorization.dto;

public record PasswordUpdateDTO(
    String oldPassword,
    String newPassword
) {
}