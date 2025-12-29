package pl.rychellos.hotel.currencyExchange;

import org.springframework.web.util.UriComponentsBuilder;

public interface ICurrencyUriBuilderProvider {
    String host();

    String path();

    default UriComponentsBuilder builder() {
        return UriComponentsBuilder.newInstance()
            .scheme("https")
            .host(host())
            .path(path());
    }
}
