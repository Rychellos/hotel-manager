package pl.rychellos.hotel.webapi;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.media.MediaService;
import pl.rychellos.hotel.media.dto.MediaDTO;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MediaService mediaService;
    @Mock
    private ApplicationExceptionFactory applicationExceptionFactory;
    @Mock
    private LangUtil langUtil;

    @InjectMocks
    private MediaController mediaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mediaController).build();
    }

    @Test
    void upload_ShouldReturnOk_WhenFileProvided() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE,
                "content".getBytes());
        MediaDTO response = new MediaDTO();
        when(mediaService.upload(any(), any(), anyBoolean())).thenReturn(response);

        // When & Then
        mockMvc.perform(multipart("/api/v1/media/upload").file(file))
                .andExpect(status().isOk());
    }
}
