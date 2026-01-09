package pl.rychellos.hotel.room.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.SearchFilter;

@Data
public class StandardFilterDTO {
    @SearchFilter
    private String name;
}
