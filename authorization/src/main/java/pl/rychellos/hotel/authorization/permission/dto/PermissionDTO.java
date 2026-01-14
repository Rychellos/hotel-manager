package pl.rychellos.hotel.authorization.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

import java.util.Set;
import java.util.UUID;

@Data
public class PermissionDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    private Set<Long> roleIds;
}
