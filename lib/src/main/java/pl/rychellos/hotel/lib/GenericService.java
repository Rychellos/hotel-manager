package pl.rychellos.hotel.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.util.Collection;

public abstract class GenericService<Entity extends BaseEntity, DTO extends BaseDTO, Filter, Repository extends GenericRepository<Entity>> implements IGenericService<Entity, DTO, Filter> {
    private final EntitySpecificationBuilder<Entity> specification = new EntitySpecificationBuilder<>();
    private final ObjectMapper objectMapper;
    private final Class<DTO> clazz;

    protected final LangUtil langUtil;
    protected final GenericMapper<Entity, DTO> mapper;
    protected final Repository repository;
    protected final ApplicationExceptionFactory exceptionFactory;

    protected GenericService(
        LangUtil langUtil, Class<DTO> clazz, GenericMapper<Entity, DTO> mapper, Repository repository,
        ApplicationExceptionFactory exceptionFactory, ObjectMapper objectMapper
    ) {
        this.langUtil = langUtil;
        this.repository = repository;
        this.mapper = mapper;
        this.exceptionFactory = exceptionFactory;
        this.clazz = clazz;
        this.objectMapper = objectMapper;
    }

    protected Specification<Entity> createSpecification(Filter currencyDTOFilter) {
        return specification.build(currencyDTOFilter);
    }

    public Page<DTO> getAllPaginated(Pageable pageable, Filter filter) {
        return repository.findAll(createSpecification(filter), pageable).map(mapper::toDTO);
    }

    public Collection<DTO> getAll(Filter filter) {
        return repository.findAll(createSpecification(filter)).stream().map(mapper::toDTO).toList();
    }

    public DTO getById(long id) throws ApplicationException {
        return mapper.toDTO(repository.findById(id).orElseThrow(() -> exceptionFactory.resourceNotFound(
                    langUtil.getMessage("error.generic.notFoundById.message")
                        .formatted(StringUtils.capitalize(clazz.getSimpleName()), id)
                )
            )
        );
    }

    public boolean exists(long id) {
        return repository.existsById(id);
    }

    public DTO save(DTO dto) {
        return mapper.toDTO(
            repository.save(
                mapper.toEntity(dto)
            )
        );
    }

    public DTO saveIfNotExists(DTO dto) throws ApplicationException {
        if (exists(dto.getId())) {
            throw exceptionFactory.conflict(langUtil.getMessage("error.generic.alreadyExists.message"));
        }

        return save(dto);
    }

    public DTO update(long id, DTO dto) throws ApplicationException {
        Entity entity = repository.findById(id)
            .orElseThrow(() -> exceptionFactory.resourceNotFound(
                    langUtil.getMessage("error.generic.notFoundById.message")
                        .formatted(StringUtils.capitalize(clazz.getSimpleName()), id)
                )
            );

        mapper.updateEntityFromDTO(entity, dto);

        return mapper.toDTO(repository.save(entity));
    }

    public DTO patch(long id, JsonPatch patch) throws ApplicationException {
        DTO user = getById(id);

        JsonNode patched;
        try {
            patched = patch.apply(objectMapper.convertValue(user, JsonNode.class));
        } catch (JsonPatchException e) {
            throw exceptionFactory.badRequest(langUtil.getMessage("error.generic.invalidJSONPatch.message"));
        }

        try {
            return this.update(id, objectMapper.treeToValue(patched, clazz));
        } catch (JsonProcessingException e) {
            throw exceptionFactory.badRequest(langUtil.getMessage("error.generic.invalidJSONPatch.message"));
        }
    }

    public void delete(long id) {
        if (exists(id)) {
            repository.deleteById(id);

            return;
        }

        throw exceptionFactory.methodNotAllowed(
            langUtil.getMessage("error.generic.notFoundById.message")
                .formatted(StringUtils.capitalize(clazz.getSimpleName()), id));
    }
}