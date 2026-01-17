package pl.rychellos.hotel.lib;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.io.IOException;
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


    public long resolveId(String idOrUuid) {
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

    public Page<DTO> getPage(
        Pageable pageable,
        Filter filter
    ) {
        return service.getAllPaginated(pageable, filter);
    }

    // GET - fetch one

    public DTO getOne(long id) {
        return service.getById(id);
    }


    public DTO getOne(UUID publicId) {
        return service.getByPublicId(publicId);
    }


    public DTO getOne(String idOrUuid) {
        return getOne(resolveId(idOrUuid));
    }

    // POST - create one

    public DTO createOne(DTO dto) {
        return service.saveIfNotExists(dto);
    }

    // PUT - update or create one

    public DTO putOne(DTO dto) {
        return service.save(dto);
    }


    public DTO putOne(String idOrUuid, DTO dto) {
        dto.setId(resolveId(idOrUuid));
        return putOne(dto);
    }

    // PATCH - partial update if exists
    public DTO patchOne(long id, JSONPatchDTO patch) {
        return service.patch(id, toProperPatch(patch));
    }

    public DTO patchOne(String idOrUuid, JSONPatchDTO patch) {
        return patchOne(resolveId(idOrUuid), patch);
    }

    // DELETE
    public void deleteOne(long id) {
        service.delete(id);
    }

    public void deleteOne(UUID publicId) {
        deleteOne(getOne(publicId).getId());
    }

    public void deleteOne(String idOrUuid) {
        deleteOne(resolveId(idOrUuid));
    }

    private JsonPatch toProperPatch(JSONPatchDTO dto) {
        try {
            // TODO: Move out
            JsonNode node = new ObjectMapper().valueToTree(dto.getBody());

            return JsonPatch.fromJson(node);
        } catch (IOException e) {
            throw applicationExceptionFactory.badRequest(langUtil.getMessage("error.generic.invalidJSONPatch.message"));
        }
    }
}
