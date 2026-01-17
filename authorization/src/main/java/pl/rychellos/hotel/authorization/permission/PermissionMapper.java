package pl.rychellos.hotel.authorization.permission;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.lib.GenericMapper;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PermissionMapper extends GenericMapper<PermissionEntity, PermissionDTO> {
    @Mapping(target = "roles", ignore = true)
    PermissionEntity toEntity(PermissionDTO dto);

    @Mapping(target = "roleIds", source = "roles")
    PermissionDTO toDTO(PermissionEntity entity);

    @Mapping(target = "roles", ignore = true)
    void updateEntityFromDTO(@MappingTarget PermissionEntity entity, PermissionDTO dto);

    default Set<Long> map(Set<RoleEntity> roles) {
        return roles == null
            ? Set.of()
            : roles.stream()
            .map(RoleEntity::getId)
            .collect(Collectors.toSet());
    }
}
