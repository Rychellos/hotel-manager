package pl.rychellos.hotel.authorization.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

@Data
public class RoleDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String internalName;
    private String publicName;
    private String description;
    private Set<Long> permissionIds;
    private Set<Long> userIds;
}
