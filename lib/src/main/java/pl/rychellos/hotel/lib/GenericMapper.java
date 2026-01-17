package pl.rychellos.hotel.lib;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

public interface GenericMapper<Entity, DTO> {
    DTO toDTO(Entity entity);

    Entity toEntity(DTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(@MappingTarget Entity entity, DTO dto);

    default Set<Long> mapEntitiesToIds(Set<? extends BaseEntity> entities) {
        return entities == null ? Set.of() : entities.stream()
            .map(BaseEntity::getId)
            .collect(Collectors.toSet());
    }
}
