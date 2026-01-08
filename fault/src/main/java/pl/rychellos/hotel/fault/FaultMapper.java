package pl.rychellos.hotel.fault;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.rychellos.hotel.fault.dto.FaultDTO;
import pl.rychellos.hotel.lib.GenericMapper;

@Mapper(componentModel = "spring")
public interface FaultMapper extends GenericMapper<FaultEntity, FaultDTO> {

    @Mapping(target = "faultTypeId", source = "faultType.id")
    @Mapping(target = "conversationId", source = "conversation.id")
    FaultDTO toDTO(FaultEntity entity);

    @Mapping(target = "faultType", ignore = true)
    @Mapping(target = "conversation", ignore = true)
    FaultEntity toEntity(FaultDTO dto);

    @Mapping(target = "faultType", ignore = true)
    @Mapping(target = "conversation", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(@MappingTarget FaultEntity entity, FaultDTO dto);
}
