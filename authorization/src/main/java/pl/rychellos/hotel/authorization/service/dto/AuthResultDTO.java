package pl.rychellos.hotel.authorization.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResultDTO {
    private AuthResponseDTO authResponseDTO;
    private String refreshToken;
    private String username;
}