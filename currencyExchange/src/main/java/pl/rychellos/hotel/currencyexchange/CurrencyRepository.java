package pl.rychellos.hotel.currencyexchange;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pl.rychellos.hotel.lib.GenericRepository;

@Repository
public interface CurrencyRepository extends GenericRepository<CurrencyEntity> {
    Optional<CurrencyEntity> findByCode(String currencyCode);

    Optional<CurrencyEntity> findByCodeAndEffectiveDate(String code, LocalDate effectiveDate);
}
