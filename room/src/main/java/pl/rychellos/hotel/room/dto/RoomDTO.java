package pl.rychellos.hotel.room.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.rychellos.hotel.lib.BaseDTO;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    private String name;
    private Long standardId;
    private Integer bedsAvailable;
    private String roomDescription;
    private BigDecimal basePriceOverride;
    private BigDecimal perPersonPriceOverride;
}
