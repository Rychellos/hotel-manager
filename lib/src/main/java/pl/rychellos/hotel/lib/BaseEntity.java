package pl.rychellos.hotel.lib;

import java.util.UUID;

public interface BaseEntity {
    Long getId();

    UUID getPublicId();

    void setPublicId(UUID publicId);
}