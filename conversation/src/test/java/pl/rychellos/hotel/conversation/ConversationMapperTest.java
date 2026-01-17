package pl.rychellos.hotel.conversation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.conversation.dto.ConversationDTO;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { ConversationMapperImpl.class })
class ConversationMapperTest {

    @Autowired
    private ConversationMapper mapper;

    @Test
    void toDTO_ShouldMapParticipantsToIds() {
        // Given
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        UserEntity user2 = new UserEntity();
        user2.setId(2L);

        ConversationEntity entity = new ConversationEntity();
        entity.setId(10L);
        entity.setPublicId(UUID.randomUUID());
        entity.setParticipants(Set.of(user1, user2));

        // When
        ConversationDTO dto = mapper.toDTO(entity);

        // Then
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getPublicId(), dto.getPublicId());
        assertEquals(2, dto.getParticipantIds().size());
        assertTrue(dto.getParticipantIds().contains(1L));
        assertTrue(dto.getParticipantIds().contains(2L));
    }

    @Test
    void toEntity_ShouldIgnoreParticipants() {
        // Given
        ConversationDTO dto = new ConversationDTO();
        dto.setId(20L);
        dto.setParticipantIds(Set.of(1L, 2L));

        // When
        ConversationEntity entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertTrue(entity.getParticipants() == null || entity.getParticipants().isEmpty());
    }
}
