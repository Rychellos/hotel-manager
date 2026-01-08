package pl.rychellos.hotel.fault;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.conversation.ConversationRepository;
import pl.rychellos.hotel.fault.dto.FaultDTO;
import pl.rychellos.hotel.fault.dto.FaultFilterDTO;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@Service
public class FaultService extends GenericService<
    FaultEntity,
    FaultDTO,
    FaultFilterDTO,
    FaultRepository
    > {
    private final FaultTypeRepository faultTypeRepository;
    private final ConversationRepository conversationRepository;

    public FaultService(
        LangUtil langUtil,
        FaultMapper mapper,
        FaultRepository repository,
        ApplicationExceptionFactory exceptionFactory,
        ObjectMapper objectMapper,
        FaultTypeRepository faultTypeRepository,
        ConversationRepository conversationRepository
    ) {
        super(langUtil, FaultDTO.class, mapper, repository, exceptionFactory, objectMapper);
        
        this.faultTypeRepository = faultTypeRepository;
        this.conversationRepository = conversationRepository;
    }

    @Override
    protected void fetchRelations(FaultEntity entity, FaultDTO dto) {
        if (dto.getFaultTypeId() != null) {
            faultTypeRepository.findById(dto.getFaultTypeId()).ifPresent(entity::setFaultType);
        }

        if (dto.getConversationId() != null) {
            conversationRepository.findById(dto.getConversationId()).ifPresent(entity::setConversation);
        }
    }
}
