package pl.rychellos.hotel.authorization.service.dto;

import java.util.Set;

public record AuthResponseDTO(
    String accessToken,
    Set<String> roles,
    Set<String> permissions) {
}
