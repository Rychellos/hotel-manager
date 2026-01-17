package pl.rychellos.hotel.conversation;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.HashSet;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.conversation.dto.MessageDTO;
import pl.rychellos.hotel.conversation.dto.MessageFilterDTO;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.media.MediaRepository;

@Service
public class MessageService extends GenericService<MessageEntity, MessageDTO, MessageFilterDTO, MessageRepository> {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;

    public MessageService(
            LangUtil langUtil,
            MessageMapper mapper,
            MessageRepository repository,
            ApplicationExceptionFactory exceptionFactory,
            ObjectMapper objectMapper,
            ConversationRepository conversationRepository,
            UserRepository userRepository,
            MediaRepository mediaRepository) {
        super(langUtil, MessageDTO.class, mapper, repository, exceptionFactory, objectMapper);
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
    }

    @Override
    protected void fetchRelations(MessageEntity entity, MessageDTO dto) throws ApplicationException {
        if (dto.getConversationId() != null) {
            conversationRepository.findById(dto.getConversationId()).ifPresent(conv -> {
                entity.setConversation(conv);
                conv.setLastActivity(LocalDateTime.now());
                conversationRepository.save(conv);
            });
        }

        if (dto.getAuthorId() != null) {
            userRepository.findById(dto.getAuthorId()).ifPresent(entity::setAuthor);
        }

        if (dto.getMediaIds() != null) {
            entity.setMediaContent(new HashSet<>(mediaRepository.findAllById(dto.getMediaIds())));
        }
    }
}
