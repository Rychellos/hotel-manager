package pl.rychellos.hotel.currencyexchange.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

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
