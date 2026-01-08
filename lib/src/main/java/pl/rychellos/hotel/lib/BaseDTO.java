package pl.rychellos.hotel.lib;

import java.util.UUID;

public interface BaseDTO {
    Long getId();

    void setId(Long id);

    UUID getPublicId();

    void setPublicId(UUID publicId);
}
