package pl.rychellos.hotel.fault.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.SearchFilter;

@Data
public class FaultTypeFilterDTO {
    @SearchFilter
    private String name;
}
