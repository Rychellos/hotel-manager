package pl.rychellos.hotel.currencyexchange;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@DataJpaTest
class CurrencyRepositoryTest {
    @Configuration
    @EnableJpaRepositories(basePackageClasses = CurrencyRepository.class)
    @EntityScan(basePackageClasses = CurrencyEntity.class)
    static class TestConfig {
    }

    @Autowired
    private CurrencyRepository currencyRepository;

    private static final LocalDate TEST_DATE = LocalDate.of(2025, 12, 23);

    @Test
    void findByCodeAndEffectiveDate_shouldReturnEntity() {
        /// Given
        CurrencyEntity entity = new CurrencyEntity(
            null,
            UUID.randomUUID(),
            "dolar amerykański",
            "USD",
            "248/A/NBP/2025",
            TEST_DATE,
            3.5848
        );
        currencyRepository.save(entity);

        /// When
        Optional<CurrencyEntity> result = currencyRepository.findByCodeAndEffectiveDate("USD", TEST_DATE);

        /// Then
        assertTrue(result.isPresent());
    }

    @Test
    void findByCode_shouldReturnEmpty_whenNotExists() {
        /// Given

        /// When
        Optional<CurrencyEntity> result = currencyRepository.findByCode("XXX");

        /// Then
        assertTrue(result.isEmpty());
    }

    @Test
    void findByCodeAndEffectiveDate_shouldReturnCurrency_whenExists() {
        /// Given
        CurrencyEntity entity = new CurrencyEntity(
            null,
            UUID.randomUUID(),
            "dolar amerykański",
            "USD",
            "248/A/NBP/2025",
            TEST_DATE,
            3.5848
        );
        currencyRepository.save(entity);

        /// When
        Optional<CurrencyEntity> result = currencyRepository.findByCodeAndEffectiveDate("USD", TEST_DATE);

        /// Then
        assertTrue(result.isPresent());
        assertEquals("USD", result.get().getCode());
        assertEquals("dolar amerykański", result.get().getCurrency());
        assertEquals(TEST_DATE, result.get().getEffectiveDate());
        assertEquals(3.5848, result.get().getMid());
    }

    @Test
    void deleteById_shouldCallRepository() {
        /// Given
        CurrencyEntity entity = new CurrencyEntity(
            null,
            UUID.randomUUID(),
            "dolar amerykański",
            "USD",
            "248/A/NBP/2025",
            TEST_DATE,
            3.5848
        );
        CurrencyEntity savedEntity = currencyRepository.save(entity);

        /// When
        currencyRepository.deleteById(savedEntity.getId());
        Optional<CurrencyEntity> result = currencyRepository.findByCodeAndEffectiveDate("USD", TEST_DATE);

        /// Then
        TestCase.assertTrue(result.isEmpty());
        assertEquals(0, currencyRepository.count());
    }

    @Test
    void findByCodeAndEffectiveDate_shouldDistinguishBetweenDifferentDates() {
        /// Given
        LocalDate date1 = LocalDate.of(2025, 12, 23);
        LocalDate date2 = LocalDate.of(2025, 12, 22);

        CurrencyEntity usd1 = new CurrencyEntity(
            null,
            UUID.randomUUID(),
            "dolar amerykański",
            "USD",
            "248/A/NBP/2025",
            date1,
            3.5848
        );
        CurrencyEntity usd2 = new CurrencyEntity(
            null,
            UUID.randomUUID(),
            "dolar amerykański",
            "USD",
            "247/A/NBP/2025",
            date2,
            3.5700
        );

        currencyRepository.save(usd1);
        currencyRepository.save(usd2);

        /// When
        Optional<CurrencyEntity> resultDate1 = currencyRepository.findByCodeAndEffectiveDate("USD", date1);
        Optional<CurrencyEntity> resultDate2 = currencyRepository.findByCodeAndEffectiveDate("USD", date2);

        /// Then
        assertTrue(resultDate1.isPresent());
        assertEquals(date1, resultDate1.get().getEffectiveDate());
        assertEquals(3.5848, resultDate1.get().getMid());

        assertTrue(resultDate2.isPresent());
        assertEquals(date2, resultDate2.get().getEffectiveDate());
        assertEquals(3.5700, resultDate2.get().getMid());
    }
}
