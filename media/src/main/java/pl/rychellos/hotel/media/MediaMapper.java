package pl.rychellos.hotel.media;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.rychellos.hotel.lib.GenericMapper;
import pl.rychellos.hotel.media.dto.MediaDTO;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true))
public interface MediaMapper extends GenericMapper<MediaEntity, MediaDTO> {

    @Mapping(target = "public", source = "public")
    MediaDTO toDTO(MediaEntity entity);

    @Mapping(target = "public", source = "public")
    @Mapping(target = "storedPath", ignore = true)
    MediaEntity toEntity(MediaDTO dto);

    @Mapping(target = "public", source = "public")
    @Mapping(target = "storedPath", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(@MappingTarget MediaEntity entity, MediaDTO dto);
}
