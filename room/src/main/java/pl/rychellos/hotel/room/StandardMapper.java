package pl.rychellos.hotel.room;

import org.mapstruct.Mapper;
import pl.rychellos.hotel.lib.GenericMapper;
import pl.rychellos.hotel.room.dto.StandardDTO;

@Mapper(componentModel = "spring")
public interface StandardMapper extends GenericMapper<StandardEntity, StandardDTO> {
}
