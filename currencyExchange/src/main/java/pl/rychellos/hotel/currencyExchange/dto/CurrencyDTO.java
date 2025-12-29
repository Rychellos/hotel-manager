package pl.rychellos.hotel.currencyExchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.rychellos.hotel.lib.BaseDTO;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDTO implements BaseDTO {
    private Long id;
    private String currency;
    private String code;

    private String no;
    private LocalDate effectiveDate;
    private Double mid;
}
