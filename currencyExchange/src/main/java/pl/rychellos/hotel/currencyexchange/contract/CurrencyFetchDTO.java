package pl.rychellos.hotel.currencyexchange.contract;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyFetchDTO {
    private String table;
    private String currency;
    private String code;
    private ArrayList<CurrencyRateDTO> rates;
}
