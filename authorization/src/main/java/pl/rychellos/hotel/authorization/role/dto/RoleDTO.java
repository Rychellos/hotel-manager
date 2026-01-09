package pl.rychellos.hotel.authorization.role.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

import java.util.Set;
import java.util.UUID;

@Data
public class RoleDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    private String name;
    private String description;
    private Set<Long> permissionIds;
    private Set<Long> userIds;
}
