package pl.rychellos.hotel.authorization.token.dto;

import lombok.Data;

@Data
public class TokenDTO {
    private Long id;
    private String username;
    private String expiryDate; // Keeping as String for simple DTO representation, or could use Instant
    private boolean revoked;
    private long accessTokenCount;
}
