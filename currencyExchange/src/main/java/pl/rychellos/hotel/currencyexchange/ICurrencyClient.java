package pl.rychellos.hotel.currencyexchange;

import pl.rychellos.hotel.currencyexchange.contract.CurrencyFetchDTO;

public interface ICurrencyClient {
    CurrencyFetchDTO getRate(String currencyCode);
}
