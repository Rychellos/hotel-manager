package pl.rychellos.hotel.currencyexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.rychellos.hotel.lib.BaseDTO;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    private String currency;
    private String code;

    private String no;
    private LocalDate effectiveDate;
    private Double mid;
}
