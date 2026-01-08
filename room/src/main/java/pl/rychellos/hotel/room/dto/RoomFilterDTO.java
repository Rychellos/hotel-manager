package pl.rychellos.hotel.room.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.SearchFilter;

@Data
public class RoomFilterDTO {
    @SearchFilter
    private String name;

    @SearchFilter(path = "standard.id")
    private Long standardId;
}
