package pl.rychellos.hotel.conversation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.conversation.dto.ConversationDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private LangUtil langUtil;
    @Mock
    private ConversationMapper conversationMapper;
    @Mock
    private ConversationRepository conversationRepository;
    @Mock
    private ApplicationExceptionFactory exceptionFactory;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UserRepository userRepository;

    private ConversationService conversationService;

    @BeforeEach
    void setUp() {
        conversationService = new ConversationService(
                langUtil, conversationMapper, conversationRepository, exceptionFactory, objectMapper, userRepository);
    }

    @Test
    void fetchRelations_ShouldSetParticipants_WhenParticipantIdsPresent() {
        // Given
        ConversationDTO dto = new ConversationDTO();
        dto.setParticipantIds(Set.of(1L, 2L));
        ConversationEntity entity = new ConversationEntity();

        when(userRepository.findAllById(dto.getParticipantIds())).thenReturn(Collections.emptyList());

        // When
        conversationService.fetchRelations(entity, dto);

        // Then
        verify(userRepository).findAllById(dto.getParticipantIds());
        assertTrue(entity.getParticipants().isEmpty());
    }
}
