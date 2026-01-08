package pl.rychellos.hotel.currencyexchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import pl.rychellos.hotel.currencyexchange.contract.CurrencyFetchDTO;
import pl.rychellos.hotel.currencyexchange.contract.CurrencyRateDTO;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyDTO;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyFilterDTO;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
public class CurrencyService extends GenericService<CurrencyEntity, CurrencyDTO, CurrencyFilterDTO, CurrencyRepository> implements ICurrencyService {
    protected CurrencyRepository repository;
    protected ICurrencyClient currencyClient;

    protected CurrencyService(
        LangUtil langUtil,
        CurrencyMapper mapper,
        CurrencyRepository repository,
        ApplicationExceptionFactory exceptionFactory,
        ObjectMapper objectMapper,
        ICurrencyClient currencyClient
    ) {
        super(langUtil, CurrencyDTO.class, mapper, repository, exceptionFactory, objectMapper);
        this.repository = repository;
        this.currencyClient = currencyClient;
    }

    //    @Cacheable(cacheNames = "currency", key = "#currencyCode + '_' + #effectiveDate")
    public CurrencyDTO get(String currencyCode, LocalDate effectiveDate) throws ApplicationException {
        Optional<CurrencyEntity> dbValue = repository.findByCodeAndEffectiveDate(currencyCode, effectiveDate);

        if (dbValue.isPresent()) {
            return mapper.toDTO(dbValue.get());
        }

        CurrencyFetchDTO currencyFetch;

        try {
            currencyFetch = currencyClient.getRate(currencyCode);
        } catch (HttpClientErrorException e) {
            throw exceptionFactory.resourceNotFound(langUtil.getMessage("error.currency.notFound.message").formatted(currencyCode));
        }

        CurrencyRateDTO currencyRate = currencyFetch.rates().getFirst();

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

    //    @CacheEvict(cacheNames = "currency", key = "#currencyCode + '_' + #effectiveDate")
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
