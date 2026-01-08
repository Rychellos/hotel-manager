package pl.rychellos.hotel.media;

import org.mapstruct.Mapper;
import pl.rychellos.hotel.lib.GenericMapper;
import pl.rychellos.hotel.media.dto.MediaDTO;

@Mapper(componentModel = "spring")
public interface MediaMapper extends GenericMapper<MediaEntity, MediaDTO> {
}
