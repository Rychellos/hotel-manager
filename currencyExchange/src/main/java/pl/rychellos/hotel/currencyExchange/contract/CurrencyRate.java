package pl.rychellos.hotel.currencyExchange.contract;

import java.time.LocalDate;

public record CurrencyRate(String no, LocalDate effectiveDate, Double mid) {
}
