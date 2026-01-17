package pl.rychellos.hotel.fault;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rychellos.hotel.fault.dto.FaultTypeDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@ExtendWith(MockitoExtension.class)
class FaultTypeServiceTest {

    @Mock
    private LangUtil langUtil;
    @Mock
    private FaultTypeMapper faultTypeMapper;
    @Mock
    private FaultTypeRepository faultTypeRepository;
    @Mock
    private ApplicationExceptionFactory exceptionFactory;
    @Mock
    private ObjectMapper objectMapper;

    private FaultTypeService faultTypeService;

    @BeforeEach
    void setUp() {
        faultTypeService = new FaultTypeService(
                langUtil, faultTypeMapper, faultTypeRepository, exceptionFactory, objectMapper);
    }

    @Test
    void fetchRelations_ShouldDoNothing() {
        // Given
        FaultTypeEntity entity = new FaultTypeEntity();
        FaultTypeDTO dto = new FaultTypeDTO();

        // When & Then
        assertDoesNotThrow(() -> faultTypeService.fetchRelations(entity, dto));
    }
}
