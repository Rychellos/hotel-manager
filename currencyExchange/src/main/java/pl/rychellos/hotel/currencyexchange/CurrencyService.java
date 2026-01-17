package pl.rychellos.hotel.currencyexchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Optional;
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

@Slf4j
@Service
public class CurrencyService extends GenericService<CurrencyEntity, CurrencyDTO, CurrencyFilterDTO, CurrencyRepository>
        implements ICurrencyService {
    protected CurrencyRepository repository;
    protected ICurrencyClient currencyClient;

    protected CurrencyService(
            LangUtil langUtil,
            CurrencyMapper mapper,
            CurrencyRepository repository,
            ApplicationExceptionFactory exceptionFactory,
            ObjectMapper objectMapper,
            ICurrencyClient currencyClient) {
        super(langUtil, CurrencyDTO.class, mapper, repository, exceptionFactory, objectMapper);
        this.repository = repository;
        this.currencyClient = currencyClient;
    }

    // @Cacheable(cacheNames = "currency", key = "#currencyCode + '_' +
    // #effectiveDate")
    public CurrencyDTO get(String currencyCode, LocalDate effectiveDate) throws ApplicationException {
        currencyCode = currencyCode.toUpperCase();
        log.info("Checking if rate for {} on day {} is present in database...", currencyCode, effectiveDate);
        Optional<CurrencyEntity> dbValue = repository.findByCodeAndEffectiveDate(currencyCode, effectiveDate);

        if (dbValue.isPresent()) {
            log.info("Rate present");
            return mapper.toDTO(dbValue.get());
        }
        log.info("Rate not present, fetching from NBP api");

        CurrencyFetchDTO currencyFetch;

        try {
            currencyFetch = currencyClient.getRate(currencyCode);
        } catch (HttpClientErrorException e) {
            throw applicationExceptionFactory.resourceNotFound(
                    langUtil.getMessage("error.currency.notFound.message").formatted(currencyCode));
        }

        CurrencyRateDTO currencyRate = currencyFetch.getRates().getFirst();

        if (currencyRate == null) {
            throw applicationExceptionFactory.resourceNotFound(
                    langUtil.getMessage("error.currency.notFound.message").formatted(currencyCode));
        }

        CurrencyDTO currencyDTO = new CurrencyDTO(
                null,
                java.util.UUID.randomUUID(),
                currencyFetch.getCurrency(),
                currencyFetch.getCode(),
                currencyRate.getNo(),
                currencyRate.getEffectiveDate(),
                currencyRate.getMid());

        return save(currencyDTO);
    }

    // @CacheEvict(cacheNames = "currency", key = "#currencyCode + '_' +
    // #effectiveDate")
    public void delete(
            String currencyCode,
            LocalDate effectiveDate) throws ApplicationException {
        super.delete(get(currencyCode, effectiveDate).getId());
    }

    @Override
    public double calculateExchangeRate(
            String currencyCode,
            LocalDate effectiveDate,
            double amount) throws ApplicationException {
        return amount / get(currencyCode, effectiveDate).getMid();
    }

    @Override
    protected void fetchRelations(CurrencyEntity entity, CurrencyDTO dto) throws ApplicationException {

    }
}
