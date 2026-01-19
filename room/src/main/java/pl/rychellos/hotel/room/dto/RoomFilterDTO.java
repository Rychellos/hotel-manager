package pl.rychellos.hotel.room.dto;

import java.math.BigDecimal;
import lombok.Data;
import pl.rychellos.hotel.lib.SearchFilter;

@Data
public class RoomFilterDTO {
    @SearchFilter(path = "name", operator = SearchFilter.Operator.LIKE_IGNORE_CASE)
    private String name;

    @SearchFilter(path = "standard.id")
    private Long standardId;

    @SearchFilter(path = "bedsAvailable", operator = SearchFilter.Operator.GREATER_THAN_EQ)
    private Integer minBedsAvailable;

    @SearchFilter(path = "basePriceOverride", operator = SearchFilter.Operator.GREATER_THAN_EQ)
    private BigDecimal minBasePrice;

    @SearchFilter(path = "basePriceOverride", operator = SearchFilter.Operator.LESS_THAN_EQ)
    private BigDecimal maxBasePrice;

    @SearchFilter(path = "perPersonPriceOverride", operator = SearchFilter.Operator.GREATER_THAN_EQ)
    private BigDecimal minPerPersonPrice;

    @SearchFilter(path = "perPersonPriceOverride", operator = SearchFilter.Operator.LESS_THAN_EQ)
    private BigDecimal maxPerPersonPrice;
}
