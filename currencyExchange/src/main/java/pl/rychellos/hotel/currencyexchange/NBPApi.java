package pl.rychellos.hotel.currencyexchange;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import pl.rychellos.hotel.currencyexchange.contract.CurrencyFetchDTO;

@HttpExchange(accept = "application/json")
public interface NBPApi extends ICurrencyClient {
    @GetExchange("/{code}")
    CurrencyFetchDTO getRate(@PathVariable String code);
}
