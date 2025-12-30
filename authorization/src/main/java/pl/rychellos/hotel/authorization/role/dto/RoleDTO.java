package pl.rychellos.hotel.authorization.role.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

import java.util.Set;

@Data
public class RoleDTO implements BaseDTO {
    private Long id;
    private String name;
    private String description;
    private Set<Long> permissionIds;
}
