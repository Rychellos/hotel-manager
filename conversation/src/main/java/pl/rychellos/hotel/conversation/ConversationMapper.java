package pl.rychellos.hotel.conversation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.rychellos.hotel.conversation.dto.ConversationDTO;
import pl.rychellos.hotel.lib.GenericMapper;

@Mapper(componentModel = "spring")
public interface ConversationMapper extends GenericMapper<ConversationEntity, ConversationDTO> {

    @Mapping(target = "participantIds", expression = "java(mapEntitiesToIds(entity.getParticipants()))")
    ConversationDTO toDTO(ConversationEntity entity);

    @Override
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "messages", ignore = true)
    ConversationEntity toEntity(ConversationDTO dto);

    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(@MappingTarget ConversationEntity entity, ConversationDTO dto);
}
