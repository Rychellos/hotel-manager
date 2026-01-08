package pl.rychellos.hotel.authorization.user.dto;

import java.util.Set;

public record UserCreateDTO(
    String username,
    String email,
    Set<Long> roleIds,
    String password
) {
}
