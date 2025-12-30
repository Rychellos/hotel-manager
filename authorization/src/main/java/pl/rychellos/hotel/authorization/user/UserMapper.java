package pl.rychellos.hotel.authorization.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.authorization.user.dto.UserDTO;
import pl.rychellos.hotel.lib.GenericMapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<UserEntity, UserDTO> {

    @Override
    @Mapping(target = "roleIds", source = "roles")
    UserDTO toDTO(UserEntity entity);

    @Override
    @Mapping(target = "roles", source = "roleIds")
    @Mapping(target = "password", ignore = true)
    UserEntity toEntity(UserDTO dto);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntityFromDTO(@MappingTarget UserEntity entity, UserDTO dto);

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
