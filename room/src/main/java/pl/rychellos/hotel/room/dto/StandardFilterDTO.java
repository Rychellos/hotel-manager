package pl.rychellos.hotel.room.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.SearchFilter;

@Data
public class StandardFilterDTO {
    @SearchFilter(path = "name", operator = SearchFilter.Operator.LIKE_IGNORE_CASE)
    private String name;
}
