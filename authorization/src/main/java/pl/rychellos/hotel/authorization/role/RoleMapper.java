package pl.rychellos.hotel.authorization.role;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.rychellos.hotel.authorization.permission.PermissionEntity;
import pl.rychellos.hotel.authorization.role.dto.RoleDTO;
import pl.rychellos.hotel.lib.GenericMapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper extends GenericMapper<RoleEntity, RoleDTO> {
    @Override
    @Mapping(target = "permissionIds", source = "permissions")
    RoleDTO toDTO(RoleEntity entity);

    @Override
    @Mapping(target = "permissions", source = "permissionIds")
    @Mapping(target = "users", ignore = true)
    RoleEntity toEntity(RoleDTO dto);

    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    void updateEntityFromDTO(@MappingTarget RoleEntity entity, RoleDTO dto);
    
    default Set<Long> mapAuthoritiesToIds(Set<PermissionEntity> authorities) {
        if (authorities == null) {
            return null;
        }

        return authorities.stream()
            .map(PermissionEntity::getId)
            .collect(Collectors.toSet());
    }

    default Set<PermissionEntity> mapIdsToAuthorities(Set<Long> ids) {
        if (ids == null) {
            return null;
        }

        return ids.stream()
            .map(id -> {
                PermissionEntity permission = new PermissionEntity();
                permission.setId(id);
                return permission;
            })
            .collect(Collectors.toSet());
    }
}
