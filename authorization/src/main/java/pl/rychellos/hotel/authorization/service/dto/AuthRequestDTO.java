package pl.rychellos.hotel.authorization.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthRequestDTO(
    @JsonProperty("username") String username,
    @JsonProperty("password") String password
) {
}
