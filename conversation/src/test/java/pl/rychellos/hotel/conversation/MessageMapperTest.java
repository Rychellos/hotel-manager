package pl.rychellos.hotel.conversation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.conversation.dto.MessageDTO;
import pl.rychellos.hotel.media.MediaEntity;

@SpringBootTest(classes = { MessageMapperImpl.class })
class MessageMapperTest {

    @Autowired
    private MessageMapper mapper;

    @Test
    void toDTO_ShouldMapRelationshipsToIds() {
        // Given
        ConversationEntity conversation = new ConversationEntity();
        conversation.setId(100L);

        UserEntity author = new UserEntity();
        author.setId(1L);

        MediaEntity media = new MediaEntity();
        media.setId(500L);

        MessageEntity entity = new MessageEntity();
        entity.setId(10L);
        entity.setPublicId(UUID.randomUUID());
        entity.setTextContent("Hello");
        entity.setConversation(conversation);
        entity.setAuthor(author);
        entity.setMediaContent(Set.of(media));
        entity.setSentTime(LocalDateTime.now());

        // When
        MessageDTO dto = mapper.toDTO(entity);

        // Then
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(100L, dto.getConversationId());
        assertEquals(1L, dto.getAuthorId());
        assertEquals(Set.of(500L), dto.getMediaIds());
    }

    @Test
    void toEntity_ShouldIgnoreRelationships() {
        // Given
        MessageDTO dto = new MessageDTO();
        dto.setId(20L);
        dto.setConversationId(100L);
        dto.setAuthorId(1L);
        dto.setMediaIds(Set.of(500L));

        // When
        MessageEntity entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertNull(entity.getConversation());
        assertNull(entity.getAuthor());
        assertTrue(entity.getMediaContent() == null || entity.getMediaContent().isEmpty());
    }
}
