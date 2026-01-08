package pl.rychellos.hotel.lib;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.util.UUID;

public abstract class GenericController<
    Entity extends BaseEntity,
    DTO extends BaseDTO,
    Filter,
    Repository extends GenericRepository<Entity>,
    Service extends GenericService<Entity, DTO, Filter, Repository>
    > {
    protected final Service service;
    protected final ApplicationExceptionFactory applicationExceptionFactory;
    protected final LangUtil langUtil;

    protected GenericController(
        Service service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        this.service = service;
        this.applicationExceptionFactory = applicationExceptionFactory;
        this.langUtil = langUtil;
    }

    protected long resolveId(String idOrUuid) {
        try {
            return Long.parseLong(idOrUuid);
        } catch (NumberFormatException e) {
            try {
                UUID publicId = UUID.fromString(idOrUuid);

                return getOne(publicId).getId();
            } catch (IllegalArgumentException ex) {
                throw applicationExceptionFactory.badRequest(
                    langUtil.getMessage("error.generic.invalidIdFormat.message")
                );
            }
        }
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

    protected DTO getOne(UUID publicId) {
        return service.getByPublicId(publicId);
    }

    protected DTO getOne(String idOrUuid) {
        return getOne(resolveId(idOrUuid));
    }

    // POST - create one
    protected DTO createOne(DTO dto) {
        return service.saveIfNotExists(dto);
    }

    // PUT - update or create one
    protected DTO putOne(DTO dto) {
        return service.save(dto);
    }

    protected DTO putOne(String idOrUuid, DTO dto) {
        dto.setId(resolveId(idOrUuid));
        return putOne(dto);
    }

    // PATCH - partial update if exists
    protected DTO patchOne(long id, JsonPatch patch) {
        return service.patch(id, patch);
    }

    protected DTO patchOne(String idOrUuid, JsonPatch patch) {
        return patchOne(resolveId(idOrUuid), patch);
    }

    // DELETE
    protected void deleteOne(long id) {
        service.delete(id);
    }

    protected void deleteOne(UUID publicId) {
        deleteOne(getOne(publicId).getId());
    }

    protected void deleteOne(String idOrUuid) {
        deleteOne(resolveId(idOrUuid));
    }
}
