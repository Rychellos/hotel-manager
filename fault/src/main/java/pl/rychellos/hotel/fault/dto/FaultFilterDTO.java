package pl.rychellos.hotel.fault.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.SearchFilter;

@Data
public class FaultFilterDTO {
    @SearchFilter(path = "faultType.id")
    private Long faultTypeId;

    @SearchFilter
    private Long reporterId;

    @SearchFilter
    private Long repairmanAssignedId;
}
