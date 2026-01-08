package pl.rychellos.hotel.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.room.dto.StandardDTO;
import pl.rychellos.hotel.room.dto.StandardFilterDTO;

@Service
public class StandardService extends GenericService<StandardEntity, StandardDTO, StandardFilterDTO, StandardRepository> {

    public StandardService(
        LangUtil langUtil,
        StandardMapper mapper,
        StandardRepository repository,
        ApplicationExceptionFactory exceptionFactory,
        ObjectMapper objectMapper
    ) {
        super(langUtil, StandardDTO.class, mapper, repository, exceptionFactory, objectMapper);
    }

    @Override
    protected void fetchRelations(StandardEntity entity, StandardDTO dto) {
    }
}
