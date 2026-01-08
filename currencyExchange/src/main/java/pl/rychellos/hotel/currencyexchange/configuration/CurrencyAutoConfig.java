package pl.rychellos.hotel.currencyexchange.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import pl.rychellos.hotel.currencyexchange.ICurrencyClient;
import pl.rychellos.hotel.currencyexchange.NBPApi;

@AutoConfiguration
@ComponentScan("pl.rychellos.hotel.currencyexchange")
public class CurrencyAutoConfig {
    @Bean
    ICurrencyClient iCurrencyClient(NBPApi api) {
        return api;
    }
}
