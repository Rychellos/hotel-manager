package pl.rychellos.hotel.currencyexchange.contract;

import java.util.ArrayList;

public record CurrencyFetch(
    String table,
    String currency,
    String code,
    ArrayList<CurrencyRate> rates
) {
}
