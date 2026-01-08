package pl.rychellos.hotel.conversation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.conversation.dto.ConversationDTO;
import pl.rychellos.hotel.conversation.dto.ConversationFilterDTO;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.util.HashSet;

@Service
public class ConversationService extends GenericService<
    ConversationEntity,
    ConversationDTO,
    ConversationFilterDTO,
    ConversationRepository
    > {
    private final UserRepository userRepository;

    public ConversationService(
        LangUtil langUtil,
        ConversationMapper mapper,
        ConversationRepository repository,
        ApplicationExceptionFactory exceptionFactory,
        ObjectMapper objectMapper,
        UserRepository userRepository
    ) {
        super(langUtil, ConversationDTO.class, mapper, repository, exceptionFactory, objectMapper);
        this.userRepository = userRepository;
    }

    @Override
    protected void fetchRelations(ConversationEntity entity, ConversationDTO dto) {
        if (dto.getParticipantIds() != null) {
            entity.setParticipants(new HashSet<>(userRepository.findAllById(dto.getParticipantIds())));
        }
    }
}
