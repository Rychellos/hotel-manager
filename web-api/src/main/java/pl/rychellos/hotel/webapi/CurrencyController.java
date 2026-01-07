package pl.rychellos.hotel.webapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
import pl.rychellos.hotel.currencyexchange.CurrencyEntity;
import pl.rychellos.hotel.currencyexchange.CurrencyRepository;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyDTO;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;


@RestController
@RequestMapping("/api/v1/currency")
public class CurrencyController extends GenericController<
    CurrencyEntity,
    CurrencyDTO,
    CurrencyFilterDTO,
    CurrencyRepository
    > {
    protected CurrencyController(
        GenericService<CurrencyEntity, CurrencyDTO, CurrencyFilterDTO, CurrencyRepository> service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping("/{id}")
    @CheckPermission(target = "CURRENCY", action = ActionType.READ, scope = ActionScope.ONE)
    public CurrencyDTO get(@PathVariable Long id) {
        return this.getOne(id);
    }
}
