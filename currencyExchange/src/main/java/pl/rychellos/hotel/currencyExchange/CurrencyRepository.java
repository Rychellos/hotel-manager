package pl.rychellos.hotel.currencyExchange;

import org.springframework.stereotype.Repository;
import pl.rychellos.hotel.lib.GenericRepository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends GenericRepository<CurrencyEntity> {
    Optional<CurrencyEntity> findByCode(String currencyCode);

    Optional<CurrencyEntity> findByCodeAndEffectiveDate(String code, LocalDate effectiveDate);
}
