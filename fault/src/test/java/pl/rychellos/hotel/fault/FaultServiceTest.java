package pl.rychellos.hotel.fault;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rychellos.hotel.conversation.ConversationEntity;
import pl.rychellos.hotel.conversation.ConversationRepository;
import pl.rychellos.hotel.fault.dto.FaultDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FaultServiceTest {

    @Mock
    private LangUtil langUtil;
    @Mock
    private FaultMapper faultMapper;
    @Mock
    private FaultRepository faultRepository;
    @Mock
    private ApplicationExceptionFactory exceptionFactory;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private FaultTypeRepository faultTypeRepository;
    @Mock
    private ConversationRepository conversationRepository;

    private FaultService faultService;

    @BeforeEach
    void setUp() {
        faultService = new FaultService(
                langUtil, faultMapper, faultRepository, exceptionFactory, objectMapper,
                faultTypeRepository, conversationRepository);
    }

    @Test
    void fetchRelations_ShouldSetFaultTypeAndConversation_WhenIdsPresent() {
        // Given
        FaultDTO dto = new FaultDTO();
        dto.setFaultTypeId(1L);
        dto.setConversationId(2L);
        FaultEntity entity = new FaultEntity();

        FaultTypeEntity faultType = new FaultTypeEntity();
        when(faultTypeRepository.findById(1L)).thenReturn(Optional.of(faultType));

        ConversationEntity conversation = new ConversationEntity();
        when(conversationRepository.findById(2L)).thenReturn(Optional.of(conversation));

        // When
        faultService.fetchRelations(entity, dto);

        // Then
        verify(faultTypeRepository).findById(1L);
        verify(conversationRepository).findById(2L);
        assertEquals(faultType, entity.getFaultType());
        assertEquals(conversation, entity.getConversation());
    }

    private void assertEquals(Object expected, Object actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }
}
