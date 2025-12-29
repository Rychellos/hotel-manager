package pl.rychellos.hotel.currencyExchange;

import pl.rychellos.hotel.currencyExchange.contract.CurrencyFetch;

public interface ICurrencyClient {
    CurrencyFetch fetchExchangeRate(String currencyCode);
}
