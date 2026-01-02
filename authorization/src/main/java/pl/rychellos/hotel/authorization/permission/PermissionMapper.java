package pl.rychellos.hotel.authorization.permission;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.lib.GenericMapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PermissionMapper extends GenericMapper<PermissionEntity, PermissionDTO> {
    @Mapping(target = "roles", source = "roleIds")
    PermissionEntity toEntity(PermissionDTO dto);

    @Mapping(target = "roleIds", source = "roles")
    PermissionDTO toDTO(PermissionEntity entity);

    @Mapping(target = "roles", source = "roleIds")
    void updateEntityFromDTO(@MappingTarget PermissionEntity entity, PermissionDTO dto);

    default Set<Long> mapRolesToIds(Set<RoleEntity> roles) {
        if (roles == null)
            return null;
        return roles.stream()
            .map(RoleEntity::getId)
            .collect(Collectors.toSet());
    }

    default Set<RoleEntity> mapIdsToRoles(Set<Long> ids) {
        if (ids == null)
            return null;
        return ids.stream()
            .map(id -> {
                RoleEntity role = new RoleEntity();
                role.setId(id);
                return role;
            })
            .collect(Collectors.toSet());
    }
}
