package pl.rychellos.hotel.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.room.dto.RoomDTO;
import pl.rychellos.hotel.room.dto.RoomFilterDTO;

@Service
public class RoomService extends GenericService<RoomEntity, RoomDTO, RoomFilterDTO, RoomRepository> {
    private final StandardRepository standardRepository;

    public RoomService(
            LangUtil langUtil,
            RoomMapper mapper,
            RoomRepository repository,
            ApplicationExceptionFactory exceptionFactory,
            ObjectMapper objectMapper,
            StandardRepository standardRepository) {
        super(langUtil, RoomDTO.class, mapper, repository, exceptionFactory, objectMapper);
        this.standardRepository = standardRepository;
    }

    @Override
    protected void fetchRelations(RoomEntity entity, RoomDTO dto) throws ApplicationException {
        if (dto.getStandardId() != null) {
            standardRepository.findById(dto.getStandardId())
                    .ifPresent(entity::setStandard);
        }
    }
}
