package pl.rychellos.hotel.authorization.role;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.rychellos.hotel.authorization.role.dto.RoleDTO;
import pl.rychellos.hotel.lib.GenericMapper;

@Mapper(componentModel = "spring")
public interface RoleMapper extends GenericMapper<RoleEntity, RoleDTO> {
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    RoleEntity toEntity(RoleDTO dto);

    @Override
    @Mapping(target = "permissionIds", source = "permissions")
    @Mapping(target = "userIds", source = "users")
    RoleDTO toDTO(RoleEntity entity);

    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    void updateEntityFromDTO(@MappingTarget RoleEntity entity, RoleDTO dto);
}
