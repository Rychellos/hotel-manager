package pl.rychellos.hotel.fault;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.rychellos.hotel.fault.dto.FaultTypeDTO;

class FaultTypeMapperTest {

    private final FaultTypeMapper mapper = Mappers.getMapper(FaultTypeMapper.class);

    @Test
    void toDTO_ShouldMapAllFields() {
        // Given
        FaultTypeEntity entity = new FaultTypeEntity();
        entity.setId(1L);
        entity.setPublicId(UUID.randomUUID());
        entity.setName("Plumbing");
        entity.setDescription("Leaking pipes");

        // When
        FaultTypeDTO dto = mapper.toDTO(entity);

        // Then
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getPublicId(), dto.getPublicId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getDescription(), dto.getDescription());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // Given
        FaultTypeDTO dto = new FaultTypeDTO();
        dto.setId(2L);
        dto.setPublicId(UUID.randomUUID());
        dto.setName("Electrical");
        dto.setDescription("Sparking outlet");

        // When
        FaultTypeEntity entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getPublicId(), entity.getPublicId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getDescription(), entity.getDescription());
    }
}
