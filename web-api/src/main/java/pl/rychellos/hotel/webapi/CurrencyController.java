package pl.rychellos.hotel.webapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
import pl.rychellos.hotel.currencyexchange.CurrencyEntity;
import pl.rychellos.hotel.currencyexchange.CurrencyRepository;
import pl.rychellos.hotel.currencyexchange.ICurrencyService;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyDTO;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/v1/currency")
public class CurrencyController extends GenericController<
    CurrencyEntity,
    CurrencyDTO,
    CurrencyFilterDTO,
    CurrencyRepository
    > {
    private final ICurrencyService iCurrencyService;

    protected CurrencyController(
        ICurrencyService service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        super((GenericService<CurrencyEntity, CurrencyDTO, CurrencyFilterDTO, CurrencyRepository>) service, applicationExceptionFactory, langUtil);
        this.iCurrencyService = service;
    }

    @GetMapping("/{code}")
    @CheckPermission(target = "CURRENCY", action = ActionType.READ, scope = ActionScope.ONE)
    public CurrencyDTO get(@PathVariable String code) {
        return iCurrencyService.get(code, LocalDate.now());
    }
}
