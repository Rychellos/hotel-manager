package pl.rychellos.hotel.room.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.rychellos.hotel.lib.BaseDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandardDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    private String name;
    private BigDecimal basePrice;
    private BigDecimal pricePerPerson;
    private String standardDescription;
}
