package pl.rychellos.hotel.fault;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.rychellos.hotel.conversation.ConversationEntity;
import pl.rychellos.hotel.fault.dto.FaultDTO;

@SpringBootTest(classes = { FaultMapperImpl.class })
class FaultMapperTest {

    @Autowired
    private FaultMapper mapper;

    @Test
    void toDTO_ShouldMapRelationshipsToIds() {
        // Given
        FaultTypeEntity type = new FaultTypeEntity();
        type.setId(5L);

        ConversationEntity conversation = new ConversationEntity();
        conversation.setId(10L);

        FaultEntity entity = new FaultEntity();
        entity.setId(1L);
        entity.setPublicId(UUID.randomUUID());
        entity.setFaultType(type);
        entity.setConversation(conversation);
        entity.setReporterId(100L);
        entity.setReported(LocalDateTime.now());

        // When
        FaultDTO dto = mapper.toDTO(entity);

        // Then
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(5L, dto.getFaultTypeId());
        assertEquals(10L, dto.getConversationId());
        assertEquals(100L, dto.getReporterId());
    }

    @Test
    void toEntity_ShouldIgnoreRelationships() {
        // Given
        FaultDTO dto = new FaultDTO();
        dto.setId(1L);
        dto.setFaultTypeId(5L);
        dto.setConversationId(10L);

        // When
        FaultEntity entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertNull(entity.getFaultType());
        assertNull(entity.getConversation());
    }
}
