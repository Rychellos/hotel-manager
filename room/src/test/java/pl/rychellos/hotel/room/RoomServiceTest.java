package pl.rychellos.hotel.room;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.room.dto.RoomDTO;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RoomMapper roomMapper;
    @Mock
    private StandardRepository standardRepository;
    @Mock
    private LangUtil langUtil;
    @Mock
    private ApplicationExceptionFactory exceptionFactory;
    @Mock
    private ObjectMapper objectMapper;

    private RoomService roomService;

    @BeforeEach
    void setUp() {
        roomService = new RoomService(langUtil, roomMapper, roomRepository, exceptionFactory, objectMapper,
                standardRepository);
    }

    @Test
    void fetchRelations_ShouldSetStandard_WhenStandardIdIsPresent() throws Exception {
        // Given
        RoomDTO dto = new RoomDTO();
        dto.setStandardId(1L);
        RoomEntity entity = new RoomEntity();
        StandardEntity standard = new StandardEntity();

        when(standardRepository.findById(1L)).thenReturn(Optional.of(standard));

        // When
        roomService.fetchRelations(entity, dto);

        // Then
        verify(standardRepository).findById(1L);
        assert (entity.getStandard() == standard);
    }

    @Test
    void fetchRelations_ShouldNotSetStandard_WhenStandardIdIsMissing() throws Exception {
        // Given
        RoomDTO dto = new RoomDTO();
        RoomEntity entity = new RoomEntity();

        // When
        roomService.fetchRelations(entity, dto);

        // Then
        verifyNoInteractions(standardRepository);
        assert (entity.getStandard() == null);
    }
}
