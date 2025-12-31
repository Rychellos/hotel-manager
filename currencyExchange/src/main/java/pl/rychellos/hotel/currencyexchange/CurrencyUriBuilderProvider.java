package pl.rychellos.hotel.currencyexchange;

import org.springframework.beans.factory.annotation.Value;

public record CurrencyUriBuilderProvider(
    String host,
    String path
) implements ICurrencyUriBuilderProvider {
    public CurrencyUriBuilderProvider(
        @Value("${currency.api.nbp.host}") String host,
        @Value("${currency.api.nbp.path}") String path
    ) {
        this.host = host;
        this.path = path;
    }
}
