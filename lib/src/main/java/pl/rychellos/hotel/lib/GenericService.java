package pl.rychellos.hotel.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.util.Collection;

public abstract class GenericService<
    Entity extends BaseEntity,
    DTO extends BaseDTO,
    Filter,
    Repository extends GenericRepository<Entity>
    > implements IGenericService<Entity, DTO, Filter> {
    private final EntitySpecificationBuilder<Entity> specification = new EntitySpecificationBuilder<>();
    private final ObjectMapper objectMapper;
    private final Class<DTO> clazz;

    protected final LangUtil langUtil;
    protected final GenericMapper<Entity, DTO> mapper;
    protected final Repository repository;
    protected final ApplicationExceptionFactory applicationExceptionFactory;

    protected GenericService(
        LangUtil langUtil,
        Class<DTO> clazz,
        GenericMapper<Entity, DTO> mapper,
        Repository repository,
        ApplicationExceptionFactory applicationExceptionFactory,
        ObjectMapper objectMapper
    ) {
        this.langUtil = langUtil;
        this.repository = repository;
        this.mapper = mapper;
        this.applicationExceptionFactory = applicationExceptionFactory;
        this.clazz = clazz;
        this.objectMapper = objectMapper;
    }

    protected abstract void fetchRelations(Entity entity, DTO dto);

    protected Specification<Entity> createSpecification(Filter currencyDTOFilter) {
        return specification.build(currencyDTOFilter);
    }

    public Page<DTO> getAllPaginated(Pageable pageable, Filter filter) {
        try {
            return repository.findAll(createSpecification(filter), pageable).map(mapper::toDTO);
        } catch (InvalidDataAccessApiUsageException exception) {
            throw applicationExceptionFactory.badRequest("Malformed api request");
        }
    }

    public Collection<DTO> getAll(Filter filter) {
        try {
            return repository.findAll(createSpecification(filter)).stream().map(mapper::toDTO).toList();
        } catch (InvalidDataAccessApiUsageException exception) {
            throw applicationExceptionFactory.badRequest("Malformed api request");
        }
    }

    public DTO getById(long id) throws ApplicationException {
        return mapper.toDTO(repository.findById(id).orElseThrow(() -> applicationExceptionFactory.resourceNotFound(
            langUtil.getMessage("error.generic.notFoundById.message")
                .formatted(StringUtils.capitalize(clazz.getSimpleName()), id))));
    }

    public DTO getByPublicId(java.util.UUID publicId) throws ApplicationException {
        return mapper.toDTO(repository.findByPublicId(publicId).orElseThrow(
            () -> applicationExceptionFactory.resourceNotFound(
                langUtil.getMessage("error.generic.notFoundByPublicId.message")
                    .formatted(StringUtils.capitalize(clazz.getSimpleName()), publicId)
            )
        ));
    }

    public boolean exists(long id) {
        return repository.existsById(id);
    }

    public boolean existsByPublicId(java.util.UUID publicId) {
        return repository.existsByPublicId(publicId);
    }

    public DTO save(DTO dto) {
        Entity entity = mapper.toEntity(dto);

        if (entity.getPublicId() == null) {
            entity.getPublicId();
        }

        fetchRelations(entity, dto);

        return mapper.toDTO(repository.save(entity));
    }

    public DTO saveIfNotExists(DTO dto) throws ApplicationException {
        if (exists(dto.getId())) {
            throw applicationExceptionFactory.conflict(langUtil.getMessage("error.generic.alreadyExists.message"));
        }

        return save(dto);
    }

    public DTO update(long id, DTO dto) throws ApplicationException {
        Entity entity = repository.findById(id).orElseThrow(
            () -> applicationExceptionFactory.resourceNotFound(
                langUtil.getMessage("error.generic.notFoundById.message")
                    .formatted(StringUtils.capitalize(clazz.getSimpleName()), id)
            )
        );

        mapper.updateEntityFromDTO(entity, dto);

        fetchRelations(entity, dto);

        return mapper.toDTO(repository.save(entity));
    }

    public DTO patch(long id, JsonPatch patch) throws ApplicationException {
        DTO dto = getById(id);

        JsonNode patched;
        try {
            patched = patch.apply(objectMapper.convertValue(dto, JsonNode.class));
        } catch (JsonPatchException e) {
            throw applicationExceptionFactory.badRequest(langUtil.getMessage("error.generic.invalidJSONPatch.message"));
        }

        try {
            return this.update(id, objectMapper.treeToValue(patched, clazz));
        } catch (JsonProcessingException e) {
            throw applicationExceptionFactory.badRequest(langUtil.getMessage("error.generic.invalidJSONPatch.message"));
        }
    }

    public void delete(long id) {
        if (exists(id)) {
            repository.deleteById(id);

            return;
        }

        throw applicationExceptionFactory.methodNotAllowed(
            langUtil.getMessage("error.generic.notFoundById.message")
                .formatted(StringUtils.capitalize(clazz.getSimpleName()), id));
    }
}