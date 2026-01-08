package pl.rychellos.hotel.lib.testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rychellos.hotel.lib.*;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest<
    Entity extends BaseEntity,
    DTO extends BaseDTO,
    Service extends GenericService<Entity, DTO, ?, Repository>,
    Repository extends GenericRepository<Entity>
    > {

    protected Repository repository;
    protected GenericMapper<Entity, DTO> mapper;
    protected Service service;

    @BeforeEach
    void setup() {
        // Setup default behavior for the strings
        lenient().when(langUtil.getMessage(anyString())).thenAnswer(i -> i.getArgument(0));
    }

    @Mock
    protected LangUtil langUtil;
    @Mock
    protected ApplicationExceptionFactory exceptionFactory;
    @Spy
    protected ObjectMapper objectMapper = new ObjectMapper();

    /// Template methods to be implemented by subclasses
    protected abstract Entity createEntity(Long id);

    protected abstract DTO createDTO(Long id);

    @Test
    void getById_ShouldReturnDto_WhenExists() {
        /// Given
        Long id = 1L;
        Entity entity = createEntity(id);
        DTO dto = createDTO(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(entity));
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        /// When
        DTO resultExisting = service.getById(id);

        /// Then
        Assertions.assertNotNull(resultExisting);
        Mockito.verify(repository).findById(id);
    }

    @Test
    void getByPublicId_ShouldReturnDto_WhenExists() {
        /// Given
        java.util.UUID publicId = java.util.UUID.randomUUID();
        Entity entity = createEntity(1L);
        DTO dto = createDTO(1L);

        Mockito.when(repository.findByPublicId(publicId)).thenReturn(Optional.of(entity));
        Mockito.when(mapper.toDTO(entity)).thenReturn(dto);

        /// When
        DTO result = service.getByPublicId(publicId);

        /// Then
        Assertions.assertNotNull(result);
        Mockito.verify(repository).findByPublicId(publicId);
    }

    @Test
    void delete_ShouldCallRepository_WhenExists() {
        /// Given
        long id = 1L;
        Mockito.when(repository.existsById(id)).thenReturn(true);

        /// When
        service.delete(id);

        /// Then
        Mockito.verify(repository).deleteById(id);
    }
}