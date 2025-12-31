package pl.rychellos.hotel.currencyexchange;

import pl.rychellos.hotel.currencyexchange.contract.CurrencyFetch;

public interface ICurrencyClient {
    CurrencyFetch fetchExchangeRate(String currencyCode);
}
