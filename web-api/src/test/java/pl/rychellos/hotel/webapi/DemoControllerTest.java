package pl.rychellos.hotel.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.rychellos.hotel.room.RoomService;
import pl.rychellos.hotel.room.StandardService;
import pl.rychellos.hotel.room.dto.StandardDTO;

@ExtendWith(MockitoExtension.class)
class DemoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StandardService standardService;
    @Mock
    private RoomService roomService;

    @InjectMocks
    private DemoController demoController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(demoController).build();
    }

    @Test
    void populateStandards_ShouldSucceed() throws Exception {
        mockMvc.perform(post("/api/v1/demo/populateStandards"))
                .andExpect(status().isOk());
    }

    @Test
    void populateRooms_ShouldSucceed() throws Exception {
        // Given
        StandardDTO standard = new StandardDTO();
        standard.setId(1L);
        when(standardService.getAll(any())).thenReturn(List.of(standard));

        // When & Then
        mockMvc.perform(post("/api/v1/demo/populateRooms"))
                .andExpect(status().isOk());
    }
}
