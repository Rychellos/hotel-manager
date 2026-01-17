package pl.rychellos.hotel.room;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.rychellos.hotel.lib.GenericMapper;
import pl.rychellos.hotel.room.dto.RoomDTO;

@Mapper(componentModel = "spring", uses = { StandardMapper.class })
public interface RoomMapper extends GenericMapper<RoomEntity, RoomDTO> {
    @Mapping(target = "standardId", source = "standard.id")
    RoomDTO toDTO(RoomEntity entity);

    @Override
    @Mapping(target = "standard", ignore = true)
    RoomEntity toEntity(RoomDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "standard", ignore = true)
    void updateEntityFromDTO(@MappingTarget RoomEntity entity, RoomDTO dto);
}
