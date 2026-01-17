package pl.rychellos.hotel.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.rychellos.hotel.media.dto.MediaDTO;

class MediaMapperTest {

    private final MediaMapper mapper = Mappers.getMapper(MediaMapper.class);

    @Test
    void toDTO_ShouldMapAllFields() {
        // Given
        MediaEntity entity = new MediaEntity();
        entity.setId(1L);
        entity.setPublicId(UUID.randomUUID());
        entity.setUrl("/api/v1/media/uuid");
        entity.setOriginalFilename("test.jpg");
        entity.setContentType("image/jpeg");
        entity.setFileSize(1024L);
        entity.setType(MediaType.IMAGE);
        entity.setOwnerId(10L);
        entity.setPublic(true);

        // When
        MediaDTO dto = mapper.toDTO(entity);

        // Then
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getPublicId(), dto.getPublicId());
        assertEquals(entity.getUrl(), dto.getUrl());
        assertEquals(entity.getOriginalFilename(), dto.getOriginalFilename());
        assertEquals(entity.getContentType(), dto.getContentType());
        assertEquals(entity.getFileSize(), dto.getFileSize());
        assertEquals(entity.getType(), dto.getType());
        assertEquals(entity.getOwnerId(), dto.getOwnerId());
        assertEquals(entity.isPublic(), dto.isPublic());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // Given
        MediaDTO dto = new MediaDTO();
        dto.setId(1L);
        dto.setPublicId(UUID.randomUUID());
        dto.setUrl("/api/v1/media/uuid");
        dto.setOriginalFilename("test.jpg");
        dto.setContentType("image/jpeg");
        dto.setFileSize(1024L);
        dto.setType(MediaType.IMAGE);
        dto.setOwnerId(10L);
        dto.setPublic(true);

        // When
        MediaEntity entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getPublicId(), entity.getPublicId());
        assertEquals(dto.getUrl(), entity.getUrl());
        assertEquals(dto.getOriginalFilename(), entity.getOriginalFilename());
        assertEquals(dto.getContentType(), entity.getContentType());
        assertEquals(dto.getFileSize(), entity.getFileSize());
        assertEquals(dto.getType(), entity.getType());
        assertEquals(dto.getOwnerId(), entity.getOwnerId());
        assertEquals(dto.isPublic(), entity.isPublic());
    }
}
