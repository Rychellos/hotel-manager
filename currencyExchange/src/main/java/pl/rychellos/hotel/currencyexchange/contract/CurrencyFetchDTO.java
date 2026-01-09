package pl.rychellos.hotel.currencyexchange.contract;

import java.util.ArrayList;

public record CurrencyFetchDTO(
    String table,
    String currency,
    String code,
    ArrayList<CurrencyRateDTO> rates
) {
}
