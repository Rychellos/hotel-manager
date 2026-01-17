package pl.rychellos.hotel.lib;

import com.github.fge.jsonpatch.JsonPatch;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;

public interface IGenericService<Entity extends BaseEntity, DTO extends BaseDTO, Filter> {
    Page<DTO> getAllPaginated(Pageable pageable, Filter filter) throws ApplicationException;

    Collection<DTO> getAll(Filter filter) throws ApplicationException;

    DTO getById(long id) throws ApplicationException;

    DTO getByPublicId(java.util.UUID publicId) throws ApplicationException;

    boolean exists(long id);

    boolean existsByPublicId(java.util.UUID publicId);

    DTO save(DTO dto) throws ApplicationException;

    DTO saveIfNotExists(DTO dto) throws ApplicationException;

    DTO update(long id, DTO dto) throws ApplicationException;

    DTO patch(long id, JsonPatch patch) throws ApplicationException;

    void delete(long id) throws ApplicationException;
}
