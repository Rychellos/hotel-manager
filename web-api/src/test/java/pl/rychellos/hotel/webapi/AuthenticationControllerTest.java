package pl.rychellos.hotel.webapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.rychellos.hotel.authorization.service.AuthService;
import pl.rychellos.hotel.authorization.service.dto.AuthRequestDTO;
import pl.rychellos.hotel.authorization.service.dto.AuthResponseDTO;
import pl.rychellos.hotel.authorization.service.dto.AuthResultDTO;
import pl.rychellos.hotel.authorization.user.UserService;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;
    @Mock
    private UserService userService;
    @Mock
    private ApplicationExceptionFactory applicationExceptionFactory;
    @Mock
    private LangUtil langUtil;

    @InjectMocks
    private AuthenticationController authenticationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void loginJson_ShouldReturnOk() throws Exception {
        // Given
        AuthResponseDTO response = new AuthResponseDTO("access-token", Set.of("ROLE_USER"), Set.of("OP_READ"));
        AuthResultDTO result = new AuthResultDTO(response, "refresh-token", "test");
        when(authService.authenticate(any(AuthRequestDTO.class))).thenReturn(result);
        when(authService.createRefreshTokenCookie(any()))
                .thenReturn(ResponseCookie.from("refresh_token", "refresh-token").build());

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"test\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    void loginForm_ShouldReturnOk() throws Exception {
        // Given
        AuthResponseDTO response = new AuthResponseDTO("access-token", Set.of("ROLE_USER"), Set.of("OP_READ"));
        AuthResultDTO result = new AuthResultDTO(response, "refresh-token", "test");
        when(authService.authenticate(any(AuthRequestDTO.class))).thenReturn(result);
        when(authService.createRefreshTokenCookie(any()))
                .thenReturn(ResponseCookie.from("refresh_token", "refresh-token").build());

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "test")
                .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.access_token").value("access-token"));
    }

    @Test
    void refresh_ShouldReturnOk() throws Exception {
        // Given
        AuthResponseDTO response = new AuthResponseDTO("new-access-token", Set.of("ROLE_USER"), Set.of("OP_READ"));
        AuthResultDTO result = new AuthResultDTO(response, "new-refresh-token", "test");
        when(authService.refreshToken("old-refresh-token")).thenReturn(result);
        when(authService.createRefreshTokenCookie(any()))
                .thenReturn(ResponseCookie.from("refresh_token", "new-refresh-token").build());

        // When & Then
        mockMvc.perform(post("/api/v1/auth/refresh")
                .cookie(new Cookie("refresh_token", "old-refresh-token")))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }

    @Test
    void logout_ShouldReturnNoContent() throws Exception {
        // Given
        when(authService.createLogoutCookie()).thenReturn(ResponseCookie.from("refresh_token", "").maxAge(0).build());

        // When & Then
        mockMvc.perform(post("/api/v1/auth/logout")
                .cookie(new Cookie("refresh_token", "some-token")))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("Set-Cookie"));
    }
}
