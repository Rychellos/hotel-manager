package pl.rychellos.hotel.lib;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import java.io.IOException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

public abstract class GenericController<Entity extends BaseEntity, DTO extends BaseDTO, Filter, Repository extends GenericRepository<Entity>, Service extends GenericService<Entity, DTO, Filter, Repository>> {
    protected final Service service;
    protected final ApplicationExceptionFactory applicationExceptionFactory;
    protected final LangUtil langUtil;

    protected GenericController(
            Service service,
            ApplicationExceptionFactory applicationExceptionFactory,
            LangUtil langUtil) {
        this.service = service;
        this.applicationExceptionFactory = applicationExceptionFactory;
        this.langUtil = langUtil;
    }

    public long resolveId(String idOrUuid) throws ApplicationException {
        try {
            return Long.parseLong(idOrUuid);
        } catch (NumberFormatException e) {
            try {
                UUID publicId = UUID.fromString(idOrUuid);

                return getOne(publicId).getId();
            } catch (IllegalArgumentException ex) {
                throw applicationExceptionFactory.badRequest(
                        langUtil.getMessage("error.generic.invalidIdFormat.message"));
            }
        }
    }

    // GET - fetch page

    public Page<DTO> getPage(
            Pageable pageable,
            Filter filter) throws ApplicationException {
        return service.getAllPaginated(pageable, filter);
    }

    // GET - fetch one

    public DTO getOne(long id) throws ApplicationException {
        return service.getById(id);
    }

    public DTO getOne(UUID publicId) throws ApplicationException {
        return service.getByPublicId(publicId);
    }

    public DTO getOne(String idOrUuid) throws ApplicationException {
        return getOne(resolveId(idOrUuid));
    }

    // POST - create one

    public DTO createOne(DTO dto) throws ApplicationException {
        return service.saveIfNotExists(dto);
    }

    // PUT - update or create one

    public DTO putOne(DTO dto) throws ApplicationException {
        return service.save(dto);
    }

    public DTO putOne(String idOrUuid, DTO dto) throws ApplicationException {
        dto.setId(resolveId(idOrUuid));
        return putOne(dto);
    }

    // PATCH - partial update if exists
    public DTO patchOne(long id, JSONPatchDTO patch) throws ApplicationException {
        return service.patch(id, toProperPatch(patch));
    }

    public DTO patchOne(String idOrUuid, JSONPatchDTO patch) throws ApplicationException {
        return patchOne(resolveId(idOrUuid), patch);
    }

    // DELETE
    public void deleteOne(long id) throws ApplicationException {
        service.delete(id);
    }

    public void deleteOne(UUID publicId) throws ApplicationException {
        deleteOne(getOne(publicId).getId());
    }

    public void deleteOne(String idOrUuid) throws ApplicationException {
        deleteOne(resolveId(idOrUuid));
    }

    private JsonPatch toProperPatch(JSONPatchDTO dto) throws ApplicationException {
        try {
            // TODO: Move out
            JsonNode node = new ObjectMapper().valueToTree(dto.getBody());

            return JsonPatch.fromJson(node);
        } catch (IOException e) {
            throw applicationExceptionFactory.badRequest(langUtil.getMessage("error.generic.invalidJSONPatch.message"));
        }
    }
}
