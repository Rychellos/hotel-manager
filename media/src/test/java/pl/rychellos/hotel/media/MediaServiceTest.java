package pl.rychellos.hotel.media;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.media.dto.MediaDTO;
import pl.rychellos.hotel.storage.StorageService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private LangUtil langUtil;
    @Mock
    private MediaMapper mediaMapper;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private ApplicationExceptionFactory exceptionFactory;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private StorageService storageService;

    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        mediaService = new MediaService(
                langUtil, mediaMapper, mediaRepository, exceptionFactory, objectMapper, storageService);
    }

    @Test
    void upload_ShouldStoreFileAndSaveMetadata() throws IOException {
        // Given
        MultipartFile file = mock(MultipartFile.class);
        byte[] bytes = "test data".getBytes();
        when(file.getOriginalFilename()).thenReturn("test.png");
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn((long) bytes.length);
        when(file.getBytes()).thenReturn(bytes);

        MediaEntity entity = new MediaEntity();
        when(mediaRepository.save(any(MediaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mediaMapper.toDTO(any(MediaEntity.class))).thenReturn(new MediaDTO());

        // When
        MediaDTO result = mediaService.upload(file, 1L, true);

        // Then
        assertNotNull(result);
        verify(storageService).store(eq(bytes), anyString());

        ArgumentCaptor<MediaEntity> entityCaptor = ArgumentCaptor.forClass(MediaEntity.class);
        verify(mediaRepository).save(entityCaptor.capture());

        MediaEntity savedEntity = entityCaptor.getValue();
        assertEquals("test.png", savedEntity.getOriginalFilename());
        assertEquals("image/png", savedEntity.getContentType());
        assertEquals(MediaType.IMAGE, savedEntity.getType());
        assertTrue(savedEntity.isPublic());
        assertEquals(1L, savedEntity.getOwnerId());
    }

    @Test
    void fetchRelations_ShouldDoNothing() {
        // Given
        MediaEntity entity = new MediaEntity();
        MediaDTO dto = new MediaDTO();

        // When
        mediaService.fetchRelations(entity, dto);

        // Then
        // No exceptions and nothing changed
    }
}
