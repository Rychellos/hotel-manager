package pl.rychellos.hotel.authorization.permission.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

import java.util.Set;

@Data
public class PermissionDTO implements BaseDTO {
    private Long id;
    private String name;
    private Set<Long> roleIds;
}
