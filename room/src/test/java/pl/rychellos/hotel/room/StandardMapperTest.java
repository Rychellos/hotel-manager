package pl.rychellos.hotel.room;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.rychellos.hotel.room.dto.StandardDTO;

class StandardMapperTest {

    private final StandardMapper mapper = Mappers.getMapper(StandardMapper.class);

    @Test
    void toDTO_ShouldMapAllFields() {
        // Given
        StandardEntity entity = new StandardEntity();
        entity.setId(1L);
        entity.setPublicId(UUID.randomUUID());
        entity.setName("Standard Name");
        entity.setBasePrice(BigDecimal.valueOf(100));
        entity.setPricePerPerson(BigDecimal.valueOf(50));
        entity.setStandardDescription("Description");

        // When
        StandardDTO dto = mapper.toDTO(entity);

        // Then
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getPublicId(), dto.getPublicId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getBasePrice(), dto.getBasePrice());
        assertEquals(entity.getPricePerPerson(), dto.getPricePerPerson());
        assertEquals(entity.getStandardDescription(), dto.getStandardDescription());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // Given
        StandardDTO dto = new StandardDTO(
                1L,
                UUID.randomUUID(),
                "Standard Name",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(50),
                "Description");

        // When
        StandardEntity entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getPublicId(), entity.getPublicId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getBasePrice(), entity.getBasePrice());
        assertEquals(dto.getPricePerPerson(), entity.getPricePerPerson());
        assertEquals(dto.getStandardDescription(), entity.getStandardDescription());
    }
}
