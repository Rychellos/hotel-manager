package pl.rychellos.hotel.webapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.room.RoomService;
import pl.rychellos.hotel.room.dto.RoomDTO;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoomService roomService;
    @Mock
    private ApplicationExceptionFactory applicationExceptionFactory;
    @Mock
    private LangUtil langUtil;

    @InjectMocks
    private RoomController roomController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void getByPublicId_ShouldReturnDto() throws Exception {
        // Given
        UUID publicId = UUID.randomUUID();
        RoomDTO dto = new RoomDTO();
        dto.setId(1L);
        dto.setPublicId(publicId);

        when(roomService.getByPublicId(publicId)).thenReturn(dto);
        when(roomService.getById(1L)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/v1/rooms/" + publicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(publicId.toString()));
    }

    @Test
    void getById_ShouldReturnDto() throws Exception {
        // Given
        RoomDTO dto = new RoomDTO();
        dto.setId(1L);
        dto.setPublicId(UUID.randomUUID());

        when(roomService.getById(1L)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/v1/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(dto.getPublicId().toString()));
    }

    @Test
    void create_ShouldReturnDto() throws Exception {
        // Given
        RoomDTO dto = new RoomDTO();
        dto.setPublicId(UUID.randomUUID());
        when(roomService.saveIfNotExists(any())).thenReturn(dto);

        // When & Then
        mockMvc.perform(post("/api/v1/rooms")
                .contentType("application/json")
                .content("{\"name\":\"New Room\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(dto.getPublicId().toString()));
    }

    @Test
    void update_ShouldReturnDto() throws Exception {
        // Given
        UUID publicId = UUID.randomUUID();
        RoomDTO dto = new RoomDTO();
        dto.setId(1L);
        dto.setPublicId(publicId);

        when(roomService.getByPublicId(publicId)).thenReturn(dto);
        when(roomService.save(any())).thenReturn(dto);

        // When & Then
        mockMvc.perform(put("/api/v1/rooms/" + publicId)
                .contentType("application/json")
                .content("{\"name\":\"Updated Room\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(publicId.toString()));
    }

    @Test
    void delete_ShouldReturnOk() throws Exception {
        // Given
        UUID publicId = UUID.randomUUID();
        RoomDTO dto = new RoomDTO();
        dto.setId(1L);
        when(roomService.getByPublicId(publicId)).thenReturn(dto);

        // When & Then
        mockMvc.perform(delete("/api/v1/rooms/" + publicId))
                .andExpect(status().isOk());
    }
}
