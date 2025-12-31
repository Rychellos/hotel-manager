package pl.rychellos.hotel.currencyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDTOFilter {
    private String currency;
    private String code;

    private LocalDate date;
}
