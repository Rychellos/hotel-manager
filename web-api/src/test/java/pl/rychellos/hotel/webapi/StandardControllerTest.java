package pl.rychellos.hotel.webapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.room.StandardService;
import pl.rychellos.hotel.room.dto.StandardDTO;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StandardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StandardService standardService;
    @Mock
    private ApplicationExceptionFactory applicationExceptionFactory;
    @Mock
    private LangUtil langUtil;

    @InjectMocks
    private StandardController standardController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(standardController).build();
    }

    @Test
    void getByPublicId_ShouldReturnDto() throws Exception {
        // Given
        UUID publicId = UUID.randomUUID();
        StandardDTO dto = new StandardDTO();
        dto.setId(1L);
        dto.setPublicId(publicId);

        when(standardService.getByPublicId(publicId)).thenReturn(dto);
        when(standardService.getById(1L)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/v1/standards/" + publicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(publicId.toString()));
    }
}
