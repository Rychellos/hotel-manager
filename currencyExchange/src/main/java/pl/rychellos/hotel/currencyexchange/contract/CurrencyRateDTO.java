package pl.rychellos.hotel.currencyexchange.contract;

import java.time.LocalDate;

public record CurrencyRateDTO(String no, LocalDate effectiveDate, Double mid) {
}
