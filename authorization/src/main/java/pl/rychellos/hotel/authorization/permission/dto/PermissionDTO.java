package pl.rychellos.hotel.authorization.permission.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

@Data
public class PermissionDTO implements BaseDTO {
    private Long id;
    private String name;
}
