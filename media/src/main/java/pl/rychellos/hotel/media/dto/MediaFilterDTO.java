package pl.rychellos.hotel.media.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.SearchFilter;
import pl.rychellos.hotel.media.MediaType;

@Data
public class MediaFilterDTO {
    @SearchFilter
    private String originalFilename;

    @SearchFilter
    private MediaType type;

    @SearchFilter
    private Long ownerId;
}
