package pl.rychellos.hotel.authorization.service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private Set<String> roles;
    private Set<String> permissions;
}
