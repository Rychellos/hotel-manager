package pl.rychellos.hotel.fault.dto;

import java.util.UUID;
import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

@Data
public class FaultTypeDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    private String name;
    private String description;
}
