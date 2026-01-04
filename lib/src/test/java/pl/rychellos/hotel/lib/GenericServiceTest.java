package pl.rychellos.hotel.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericServiceTest {

    // Concrete implementations for testing abstract classes
    private static class TestEntity implements BaseEntity {
        public Long getId() {
            return 1L;
        }
    }

    private static class TestDTO implements BaseDTO {
        public Long getId() {
            return 1L;
        }
    }

    private static class TestFilter {
    }

    private interface TestRepository extends GenericRepository<TestEntity> {
    }

    private class TestService extends GenericService<TestEntity, TestDTO, TestFilter, TestRepository> {
        public TestService(LangUtil langUtil, GenericMapper<TestEntity, TestDTO> mapper,
                           TestRepository repository, ApplicationExceptionFactory exceptionFactory,
                           ObjectMapper objectMapper) {
            super(langUtil, TestDTO.class, mapper, repository, exceptionFactory, objectMapper);
        }

        @Override
        protected void fetchRelations(TestEntity entity, TestDTO dto) {
            // No-op for base testing
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
    void getById_ShouldReturnDto_WhenEntityExists() {
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
        when(exceptionFactory.resourceNotFound(anyString())).thenReturn(new ApplicationException("Title", "Detail", null));

        // When & Then
        assertThrows(ApplicationException.class, () -> service.getById(1L));
        verify(exceptionFactory).resourceNotFound(anyString());
    }

    @Test
    void delete_ShouldCallRepository_WhenExists() {
        // Given
        when(repository.existsById(1L)).thenReturn(true);

        // When
        service.delete(1L);

        // Then
        verify(repository).deleteById(1L);
    }
}