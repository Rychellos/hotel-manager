package pl.rychellos.hotel.webapi;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.rychellos.hotel.fault.FaultTypeService;
import pl.rychellos.hotel.fault.dto.FaultTypeDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

class FaultTypeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FaultTypeService faultTypeService;

    @Mock
    private ApplicationExceptionFactory exceptionFactory;

    @Mock
    private LangUtil langUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        FaultTypeController controller = new FaultTypeController(faultTypeService, exceptionFactory, langUtil);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getByPublicId_ShouldReturnDto() throws Exception {
        // Given
        UUID publicId = UUID.randomUUID();
        FaultTypeDTO dto = new FaultTypeDTO();
        dto.setId(1L);
        dto.setPublicId(publicId);
        dto.setName("HYDRAULIC");

        when(faultTypeService.getByPublicId(publicId)).thenReturn(dto);
        when(faultTypeService.getById(1L)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/v1/fault-types/" + publicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(publicId.toString()))
                .andExpect(jsonPath("$.name").value("HYDRAULIC"));
    }
}
