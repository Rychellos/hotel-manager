package pl.rychellos.hotel.currencyexchange.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRateDTO {
    private String no;
    private LocalDate effectiveDate;
    private Double mid;
}
