package pl.rychellos.hotel.authorization.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.rychellos.hotel.authorization.user.dto.UserDTO;
import pl.rychellos.hotel.lib.GenericMapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<UserEntity, UserDTO> {
    @Mapping(target = "roleIds", source = "roles")
    UserDTO toDTO(UserEntity entity);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    UserEntity toEntity(UserDTO dto);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateEntityFromDTO(@MappingTarget UserEntity entity, UserDTO dto);
}
