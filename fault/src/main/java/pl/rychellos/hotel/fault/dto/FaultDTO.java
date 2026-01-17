package pl.rychellos.hotel.fault.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

@Data
public class FaultDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    private Long faultTypeId;
    private Long reporterId;
    private Long repairmanAssignedId;
    private LocalDateTime reported;
    private LocalDateTime repaired;
    private Long conversationId;
}
