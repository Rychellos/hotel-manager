package pl.rychellos.hotel.fault;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.fault.dto.FaultTypeDTO;
import pl.rychellos.hotel.fault.dto.FaultTypeFilterDTO;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@Service
public class FaultTypeService
        extends GenericService<FaultTypeEntity, FaultTypeDTO, FaultTypeFilterDTO, FaultTypeRepository> {
    public FaultTypeService(
            LangUtil langUtil,
            FaultTypeMapper mapper,
            FaultTypeRepository repository,
            ApplicationExceptionFactory exceptionFactory,
            ObjectMapper objectMapper) {
        super(langUtil, FaultTypeDTO.class, mapper, repository, exceptionFactory, objectMapper);
    }

    @Override
    protected void fetchRelations(FaultTypeEntity entity, FaultTypeDTO dto) throws ApplicationException {
    }
}
