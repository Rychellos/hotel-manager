package pl.rychellos.hotel.currencyExchange.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;
import pl.rychellos.hotel.currencyExchange.CurrencyClient;
import pl.rychellos.hotel.currencyExchange.CurrencyUriBuilderProvider;
import pl.rychellos.hotel.currencyExchange.ICurrencyClient;
import pl.rychellos.hotel.currencyExchange.ICurrencyUriBuilderProvider;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@Configuration
public class CurrencyClientConfig {
    @Bean
    public ICurrencyUriBuilderProvider currencyClientUriBuilderProvider(
        @Value("${currency.api.nbp.host}") String apiHost,
        @Value("${currency.api.nbp.path}") String apiPath
    ) {
        return new CurrencyUriBuilderProvider(apiHost, apiPath);
    }

    @Bean
    @Scope("prototype")
    ICurrencyClient currencyClient(ApplicationExceptionFactory applicationExceptionFactory, LangUtil langUtil, ICurrencyUriBuilderProvider uriBuilderProvider) {
        return new CurrencyClient(applicationExceptionFactory, langUtil, uriBuilderProvider, RestClient.builder().build());
    }
}
