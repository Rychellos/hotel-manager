package pl.rychellos.hotel.currencyexchange.contract;

import java.time.LocalDate;

public record CurrencyRate(String no, LocalDate effectiveDate, Double mid) {
}
