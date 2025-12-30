package pl.rychellos.hotel.authorization.permission;

import org.mapstruct.Mapper;
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.lib.GenericMapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper extends GenericMapper<PermissionEntity, PermissionDTO> {
}
