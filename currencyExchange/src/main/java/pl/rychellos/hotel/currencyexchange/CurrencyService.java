package pl.rychellos.hotel.currencyexchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.currencyexchange.contract.CurrencyFetch;
import pl.rychellos.hotel.currencyexchange.contract.CurrencyRate;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyDTO;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyDTOFilter;
import pl.rychellos.hotel.lib.GenericMapper;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CurrencyService extends GenericService<CurrencyEntity, CurrencyDTO, CurrencyDTOFilter, CurrencyRepository> implements ICurrencyService {
    protected CurrencyRepository repository;
    protected ICurrencyClient currencyClient;

    protected CurrencyService(
        LangUtil langUtil,
        Class<CurrencyDTO> clazz,
        GenericMapper<CurrencyEntity, CurrencyDTO> mapper,
        CurrencyRepository repository,
        ApplicationExceptionFactory exceptionFactory,
        ObjectMapper objectMapper,
        ICurrencyClient currencyClient
    ) {
        super(langUtil, clazz, mapper, repository, exceptionFactory, objectMapper);
        this.repository = repository;
        this.currencyClient = currencyClient;
    }

    @Cacheable(cacheNames = "currency", key = "#currencyCode + '_' + #effectiveDate")
    public CurrencyDTO get(String currencyCode, LocalDate effectiveDate) throws ApplicationException {
        Optional<CurrencyEntity> dbValue = repository.findByCodeAndEffectiveDate(currencyCode, effectiveDate);

        if (dbValue.isPresent()) {
            return mapper.toDTO(dbValue.get());
        }

        CurrencyFetch currencyFetch = currencyClient.fetchExchangeRate(currencyCode);

        CurrencyRate currencyRate = currencyFetch.rates().getFirst();

        if (currencyRate == null) {
            throw exceptionFactory.resourceNotFound(langUtil.getMessage("error.currency.notFound.message").formatted(currencyCode));
        }

        CurrencyDTO currencyDTO = new CurrencyDTO(
            null,
            currencyFetch.currency(),
            currencyFetch.code(),
            currencyRate.no(),
            currencyRate.effectiveDate(),
            currencyRate.mid()
        );

        return save(currencyDTO);
    }

    @CacheEvict(cacheNames = "currency", key = "#currencyCode + '_' + #effectiveDate")
    public void delete(String currencyCode, LocalDate effectiveDate) throws ApplicationException {
        super.delete(get(currencyCode, effectiveDate).getId());
    }

    @Override
    public double calculateExchangeRate(String currencyCode, LocalDate effectiveDate, double amount) throws ApplicationException {
        return amount / get(currencyCode, effectiveDate).getMid();
    }

    @Override
    protected void fetchRelations(CurrencyEntity entity, CurrencyDTO dto) {

    }
}
