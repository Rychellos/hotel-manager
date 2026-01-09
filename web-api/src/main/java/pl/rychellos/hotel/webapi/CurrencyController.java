package pl.rychellos.hotel.webapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
import pl.rychellos.hotel.currencyexchange.CurrencyEntity;
import pl.rychellos.hotel.currencyexchange.CurrencyRepository;
import pl.rychellos.hotel.currencyexchange.CurrencyService;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyDTO;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/v1/currency")
@Tag(name = "Exchange Rate", description = "Endpoint for fetching currency exchange rates provided by The National Bank of Poland")
public class CurrencyController extends GenericController<
    CurrencyEntity,
    CurrencyDTO,
    CurrencyFilterDTO,
    CurrencyRepository,
    CurrencyService
    > {

    protected CurrencyController(
        CurrencyService service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping("/{code}")
    @CheckPermission(target = "CURRENCY", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetches today's exchange rate in relation to PLN")
    public CurrencyDTO get(@PathVariable String code) {
        return service.get(code, LocalDate.now());
    }
}
