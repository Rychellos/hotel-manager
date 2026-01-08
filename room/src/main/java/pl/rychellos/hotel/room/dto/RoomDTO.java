package pl.rychellos.hotel.room.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

import java.math.BigDecimal;

@Data
public class RoomDTO implements BaseDTO {
    private Long id;
    private java.util.UUID publicId;
    private String name;
    private StandardDTO standard;
    private Long standardId;
    private Integer bedsAvailable;
    private String roomDescription;
    private BigDecimal basePriceOverride;
    private BigDecimal perPersonPriceOverride;
}
