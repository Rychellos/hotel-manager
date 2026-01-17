package pl.rychellos.hotel.room;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.rychellos.hotel.room.dto.RoomDTO;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// Since RoomMapper uses StandardMapper, and they are both MapStruct mappers with componentModel = "spring",
// it might be easier to test them in a Spring context or manually wire them.
// However, to keep it as a unit test, we can try to use ReflectionTestUtils or similar if needed,
// but MapStruct usually needs the other mapper to be present.
// Let's check how StandardMapper is injected into RoomMapper.
// In RoomMapper.java: @Mapper(componentModel = "spring", uses = {StandardMapper.class})

@SpringBootTest(classes = { RoomMapperImpl.class, StandardMapperImpl.class })
class RoomMapperTest {

    @Autowired
    private RoomMapper mapper;

    @Test
    void toDTO_ShouldMapAllFieldsIncludingStandardId() {
        // Given
        StandardEntity standard = new StandardEntity();
        standard.setId(5L);

        RoomEntity entity = new RoomEntity();
        entity.setId(1L);
        entity.setPublicId(UUID.randomUUID());
        entity.setName("Room Name");
        entity.setBedsAvailable(2);
        entity.setBasePriceOverride(BigDecimal.valueOf(120));
        entity.setPerPersonPriceOverride(BigDecimal.valueOf(60));
        entity.setRoomDescription("Room Desc");
        entity.setStandard(standard);

        // When
        RoomDTO dto = mapper.toDTO(entity);

        // Then
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getPublicId(), dto.getPublicId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getBedsAvailable(), dto.getBedsAvailable());
        assertEquals(entity.getBasePriceOverride(), dto.getBasePriceOverride());
        assertEquals(entity.getPerPersonPriceOverride(), dto.getPerPersonPriceOverride());
        assertEquals(entity.getRoomDescription(), dto.getRoomDescription());
        assertEquals(5L, dto.getStandardId());
    }

    @Test
    void toEntity_ShouldMapAllFieldsButIgnoreStandard() {
        // Given
        RoomDTO dto = new RoomDTO(
                1L,
                UUID.randomUUID(),
                "Room Name",
                5L,
                2,
                "Room Desc",
                BigDecimal.valueOf(120),
                BigDecimal.valueOf(60));

        // When
        RoomEntity entity = mapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getPublicId(), entity.getPublicId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getBedsAvailable(), entity.getBedsAvailable());
        assertEquals(dto.getBasePriceOverride(), entity.getBasePriceOverride());
        assertEquals(dto.getPerPersonPriceOverride(), entity.getPerPersonPriceOverride());
        assertEquals(dto.getRoomDescription(), entity.getRoomDescription());
        assertNull(entity.getStandard(), "Standard should be fetched by service, not mapper");
    }

    @Test
    void updateEntityFromDTO_ShouldUpdateFields() {
        // Given
        RoomEntity entity = new RoomEntity();
        entity.setId(1L);
        entity.setName("Old Name");

        RoomDTO dto = new RoomDTO();
        dto.setName("New Name");
        dto.setBedsAvailable(3);

        // When
        mapper.updateEntityFromDTO(entity, dto);

        // Then
        assertEquals(1L, entity.getId(), "ID should be ignored in update");
        assertEquals("New Name", entity.getName());
        assertEquals(3, entity.getBedsAvailable());
    }
}
