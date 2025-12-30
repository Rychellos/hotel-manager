package pl.rychellos.hotel.currencyExchange.contract;

import java.util.ArrayList;

public record CurrencyFetch(
    String table,
    String currency,
    String code,
    ArrayList<CurrencyRate> rates
) {
}
