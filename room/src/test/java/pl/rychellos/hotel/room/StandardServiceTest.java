package pl.rychellos.hotel.room;

import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.room.dto.StandardDTO;

@ExtendWith(MockitoExtension.class)
class StandardServiceTest {

    @Mock
    private StandardRepository standardRepository;
    @Mock
    private StandardMapper standardMapper;
    @Mock
    private LangUtil langUtil;
    @Mock
    private ApplicationExceptionFactory exceptionFactory;
    @Mock
    private ObjectMapper objectMapper;

    private StandardService standardService;

    @BeforeEach
    void setUp() {
        standardService = new StandardService(langUtil, standardMapper, standardRepository, exceptionFactory,
                objectMapper);
    }

    @Test
    void fetchRelations_ShouldDoNothing() throws Exception {
        // Given
        StandardDTO dto = new StandardDTO(1L, UUID.randomUUID(), "Standard", BigDecimal.TEN, BigDecimal.ONE, "Desc");
        StandardEntity entity = new StandardEntity();

        // When
        standardService.fetchRelations(entity, dto);

        // Then
        verifyNoInteractions(standardRepository);
    }
}
