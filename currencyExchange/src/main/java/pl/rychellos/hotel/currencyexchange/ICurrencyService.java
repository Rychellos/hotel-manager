package pl.rychellos.hotel.currencyexchange;

import java.time.LocalDate;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyDTO;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyFilterDTO;
import pl.rychellos.hotel.lib.IGenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;

public interface ICurrencyService extends IGenericService<CurrencyEntity, CurrencyDTO, CurrencyFilterDTO> {
    /**
     * Gets cached value of currency rate from the database for a given day. If
     * entry is not found will try to fetch
     * from specified api.
     *
     * @param currencyCode ISO 4217 Numeric Currency Code
     * @return DTO to handle exchange rates
     */
    CurrencyDTO get(String currencyCode, LocalDate effectiveDate) throws ApplicationException;

    void delete(String currencyCode, LocalDate effectiveDate) throws ApplicationException;

    /**
     * Calculates exchange rate from PLN to chosen currency.
     *
     * @param currencyCode ISO 4217 Numeric Currency Code
     * @param amount       Amount to exchange
     * @return Amount in chosen currency
     */
    double calculateExchangeRate(String currencyCode, LocalDate effectiveDate, double amount)
            throws ApplicationException;
}
