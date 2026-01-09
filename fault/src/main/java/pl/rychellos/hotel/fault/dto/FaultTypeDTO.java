package pl.rychellos.hotel.fault.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

import java.util.UUID;

@Data
public class FaultTypeDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    private String name;
    private String description;
}
