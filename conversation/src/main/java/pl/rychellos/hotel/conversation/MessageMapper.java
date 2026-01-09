package pl.rychellos.hotel.conversation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.rychellos.hotel.conversation.dto.MessageDTO;
import pl.rychellos.hotel.lib.GenericMapper;

@Mapper(componentModel = "spring")
public interface MessageMapper extends GenericMapper<MessageEntity, MessageDTO> {

    @Mapping(target = "mediaIds", expression = "java(mapEntitiesToIds(entity.getMediaContent()))")
    @Mapping(target = "conversationId", source = "conversation.id")
    @Mapping(target = "authorId", source = "author.id")
    MessageDTO toDTO(MessageEntity entity);

    @Mapping(target = "mediaContent", ignore = true)
    @Mapping(target = "conversation", ignore = true)
    @Mapping(target = "author", ignore = true)
    MessageEntity toEntity(MessageDTO dto);

    @Mapping(target = "mediaContent", ignore = true)
    @Mapping(target = "conversation", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(@MappingTarget MessageEntity entity, MessageDTO dto);
}
