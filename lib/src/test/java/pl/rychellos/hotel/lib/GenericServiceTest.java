package pl.rychellos.hotel.lib;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@ExtendWith(MockitoExtension.class)
class GenericServiceTest {
    private static class TestEntity implements BaseEntity {
        @Getter
        private Long id = 1L;
        @Getter
        @Setter
        private UUID publicId = UUID.randomUUID();
    }

    @Getter
    @Setter
    private static class TestDTO implements BaseDTO {
        private Long id = 1L;
        private UUID publicId = UUID.randomUUID();
    }

    private static class TestFilter {
    }

    private interface TestRepository extends GenericRepository<TestEntity> {
    }

    private static class TestService extends GenericService<TestEntity, TestDTO, TestFilter, TestRepository> {
        public TestService(
                LangUtil langUtil,
                GenericMapper<TestEntity, TestDTO> mapper,
                TestRepository repository,
                ApplicationExceptionFactory exceptionFactory,
                ObjectMapper objectMapper) {
            super(langUtil, TestDTO.class, mapper, repository, exceptionFactory, objectMapper);
        }

        @Override
        protected void fetchRelations(TestEntity entity, TestDTO dto) throws ApplicationException {
            // No-op for base testing
        }

        @Override
        public TestDTO getByPublicId(UUID publicId) throws ApplicationException {
            return super.getByPublicId(publicId);
        }

        @Override
        public boolean existsByPublicId(UUID publicId) {
            return super.existsByPublicId(publicId);
        }
    }

    @Mock
    private TestRepository repository;
    @Mock
    private GenericMapper<TestEntity, TestDTO> mapper;
    @Mock
    private ApplicationExceptionFactory exceptionFactory;
    @Mock
    private LangUtil langUtil;

    private TestService service;

    @BeforeEach
    void setUp() {
        service = new TestService(langUtil, mapper, repository, exceptionFactory, new ObjectMapper());
    }

    @Test
    void getById_ShouldReturnDto_WhenEntityExists() throws ApplicationException {
        // Given
        TestEntity entity = new TestEntity();
        TestDTO dto = new TestDTO();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(dto);

        // When
        TestDTO result = service.getById(1L);

        // Then
        assertNotNull(result);
        verify(repository).findById(1L);
    }

    @Test
    void getById_ShouldThrowException_WhenEntityDoesNotExist() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.empty());
        when(langUtil.getMessage(anyString())).thenReturn("Not Found %s %s");
        when(exceptionFactory.resourceNotFound(anyString()))
                .thenReturn(new ApplicationException("Title", "Detail", null));

        // When & Then
        assertThrows(ApplicationException.class, () -> service.getById(1L));
        verify(exceptionFactory).resourceNotFound(anyString());
    }

    @Test
    void delete_ShouldCallRepository_WhenExists() throws ApplicationException {
        // Given
        when(repository.existsById(1L)).thenReturn(true);

        // When
        service.delete(1L);

        // Then
        verify(repository).deleteById(1L);
    }

    @Test
    void getByPublicId_ShouldReturnDto_WhenEntityExists() throws ApplicationException {
        // Given
        UUID publicId = UUID.randomUUID();
        TestEntity entity = new TestEntity();
        TestDTO dto = new TestDTO();
        when(repository.findByPublicId(publicId)).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(dto);

        // When
        TestDTO result = service.getByPublicId(publicId);

        // Then
        assertNotNull(result);
        verify(repository).findByPublicId(publicId);
    }

    @Test
    void existsByPublicId_ShouldReturnTrue_WhenExists() {
        // Given
        UUID publicId = UUID.randomUUID();
        when(repository.existsByPublicId(publicId)).thenReturn(true);

        // When
        boolean result = service.existsByPublicId(publicId);

        // Then
        assertTrue(result);
        verify(repository).existsByPublicId(publicId);
    }
}
