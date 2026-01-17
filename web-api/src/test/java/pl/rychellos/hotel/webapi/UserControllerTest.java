package pl.rychellos.hotel.webapi;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import pl.rychellos.hotel.authorization.user.UserService;
import pl.rychellos.hotel.authorization.user.dto.UserDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;
    @Mock
    private ApplicationExceptionFactory applicationExceptionFactory;
    @Mock
    private LangUtil langUtil;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void getByPublicId_ShouldReturnDto() throws Exception {
        // Given
        UUID publicId = UUID.randomUUID();
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setPublicId(publicId);

        when(userService.getByPublicId(publicId)).thenReturn(dto);
        when(userService.getById(1L)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/v1/users/" + publicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(publicId.toString()));
    }
}
