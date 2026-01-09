package pl.rychellos.hotel.media.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;
import pl.rychellos.hotel.media.MediaType;

import java.util.UUID;

@Data
public class MediaDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    private String url;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
    private MediaType type;
    private Long ownerId;
    private boolean isPublic;
}
