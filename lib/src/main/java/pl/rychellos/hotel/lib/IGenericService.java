package pl.rychellos.hotel.lib;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;

import java.util.Collection;

public interface IGenericService<Entity extends BaseEntity, DTO extends BaseDTO, Filter> {
    Page<DTO> getAllPaginated(Pageable pageable, Filter filter);

    Collection<DTO> getAll(Filter filter);

    DTO getById(long id) throws ApplicationException;

    boolean exists(long id);

    DTO save(DTO dto);

    DTO update(long id, DTO dto);

    DTO patch(long id, JsonPatch patch) throws ApplicationException;

    void deleteOne(long id);
}
