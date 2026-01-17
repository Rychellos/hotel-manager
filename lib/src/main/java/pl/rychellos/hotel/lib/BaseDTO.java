package pl.rychellos.hotel.lib;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public interface BaseDTO {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    Long getId();

    void setId(Long id);

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    UUID getPublicId();

    void setPublicId(UUID publicId);
}
