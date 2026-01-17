package pl.rychellos.hotel.currencyexchange.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyFilterDTO {
    private String currency;
    private String code;

    private LocalDate date;
}
