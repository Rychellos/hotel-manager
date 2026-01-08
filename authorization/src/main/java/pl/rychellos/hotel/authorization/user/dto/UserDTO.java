package pl.rychellos.hotel.authorization.user.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

import java.util.Set;
import java.util.UUID;

@Data
public class UserDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    private String username;
    private String email;
    private Set<Long> roleIds;
}
