package pl.rychellos.hotel.lib;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

public interface GenericMapper<Entity, DTO> {
    DTO toDTO(Entity entity);

    Entity toEntity(DTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(@MappingTarget Entity entity, DTO dto);
}
