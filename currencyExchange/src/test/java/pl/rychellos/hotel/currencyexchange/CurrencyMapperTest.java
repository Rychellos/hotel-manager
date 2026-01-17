package pl.rychellos.hotel.currencyexchange;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyDTO;

class CurrencyMapperTest {
    private final CurrencyMapper mapper = Mappers.getMapper(CurrencyMapper.class);
    ;

    @Test
    void shouldMapEntityToDto() {
        /// Given
        CurrencyEntity entity = new CurrencyEntity(
            1L,
            UUID.randomUUID(),
            "US Dollar",
            "USD",
            "001/A/2024",
            LocalDate.now(),
            4.01
        );

        /// When
        CurrencyDTO dto = mapper.toDTO(entity);

        /// Then
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getCurrency(), dto.getCurrency());
        assertEquals(entity.getCode(), dto.getCode());
        assertEquals(entity.getNo(), dto.getNo());
        assertEquals(entity.getEffectiveDate(), dto.getEffectiveDate());
        assertEquals(entity.getMid(), dto.getMid());
    }

    @Test
    void shouldMapDtoToEntity() {
        /// Given
        CurrencyDTO dto = new CurrencyDTO(
            2L,
            UUID.randomUUID(),
            "Euro",
            "EUR",
            "002/B/2024",
            LocalDate.of(2024, 1, 1),
            4.35
        );

        /// When
        CurrencyEntity entity = mapper.toEntity(dto);

        /// Then
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getCurrency(), entity.getCurrency());
        assertEquals(dto.getCode(), entity.getCode());
        assertEquals(dto.getNo(), entity.getNo());
        assertEquals(dto.getEffectiveDate(), entity.getEffectiveDate());
        assertEquals(dto.getMid(), entity.getMid());
    }

    @Test
    void shouldReturnNullWhenSourceIsNull() {
        assertNull(mapper.toDTO(null));
        assertNull(mapper.toEntity(null));
    }
}
