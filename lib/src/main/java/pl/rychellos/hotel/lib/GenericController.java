package pl.rychellos.hotel.lib;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

public abstract class GenericController<Entity extends BaseEntity, DTO extends BaseDTO, Filter, Repository extends GenericRepository<Entity>> {
    protected final GenericService<Entity, DTO, Filter, Repository> service;
    protected final ApplicationExceptionFactory applicationExceptionFactory;
    protected final LangUtil langUtil;

    protected GenericController(
        GenericService<Entity, DTO, Filter, Repository> service, ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        this.service = service;
        this.applicationExceptionFactory = applicationExceptionFactory;
        this.langUtil = langUtil;
    }

    // GET - fetch page
    protected Page<DTO> getPage(
        Pageable pageable,
        Filter filter
    ) {
        return service.getAllPaginated(pageable, filter);
    }

    // GET - fetch one
    protected DTO getOne(long id) {
        return service.getById(id);
    }

    // POST - create one
    protected DTO createOne(DTO dto) {
        return service.saveIfNotExists(dto);
    }

    // PUT - update or create one
    protected DTO putOne(DTO dto) {
        return service.save(dto);
    }

    // PATCH - partial update if exists
    protected DTO patchOne(long id, JsonPatch patch) {
        return service.patch(id, patch);
    }

    // DELETE
    protected void deleteOne(long id) {
        service.delete(id);
    }
}
