package pl.rychellos.hotel.conversation;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.conversation.dto.MessageDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.media.MediaRepository;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private LangUtil langUtil;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ApplicationExceptionFactory exceptionFactory;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ConversationRepository conversationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MediaRepository mediaRepository;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(
                langUtil, messageMapper, messageRepository, exceptionFactory, objectMapper,
                conversationRepository, userRepository, mediaRepository);
    }

    @Test
    void fetchRelations_ShouldSetConversationAndAuthor_WhenIdsPresent() throws Exception {
        // Given
        MessageDTO dto = new MessageDTO();
        dto.setConversationId(1L);
        dto.setAuthorId(2L);
        dto.setMediaIds(Set.of(3L));
        MessageEntity entity = new MessageEntity();

        ConversationEntity conversation = new ConversationEntity();
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        when(mediaRepository.findAllById(dto.getMediaIds())).thenReturn(Collections.emptyList());

        // When
        messageService.fetchRelations(entity, dto);

        // Then
        verify(conversationRepository).findById(1L);
        verify(conversationRepository).save(conversation);
        verify(userRepository).findById(2L);
        verify(mediaRepository).findAllById(dto.getMediaIds());
    }
}
