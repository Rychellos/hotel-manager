package pl.rychellos.hotel.currencyExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import pl.rychellos.hotel.currencyExchange.contract.CurrencyFetch;
import pl.rychellos.hotel.currencyExchange.contract.CurrencyRate;
import pl.rychellos.hotel.currencyExchange.dto.CurrencyDTO;
import pl.rychellos.hotel.lib.GenericMapper;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CurrencyServiceTest {

    @Mock
    private LangUtil langUtil;

    @Mock
    private GenericMapper<CurrencyEntity, CurrencyDTO> mapper;

    @Mock
    private CurrencyRepository repository;

    @Mock
    private ApplicationExceptionFactory exceptionFactory;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ICurrencyClient currencyClient;

    @Mock
    private CacheManager cacheManager;

    // @Spy + @InjectMocks = Real service instance with injected mocks + partial real behavior
    @Spy
    @InjectMocks
    private CurrencyService currencyService;

    private static final String TEST_CURRENCY_CODE = "USD";
    private static final Long TEST_ID = 1L;
    private static final String TEST_NO = "248/A/NBP/2025";
    private static final LocalDate TEST_EFFECTIVE_DATE = LocalDate.of(2025, 12, 23);
    private static final Double TEST_MID = 3.5848;

    @BeforeEach
    void setUp() {
        // No manual instantiation needed - @InjectMocks handles it
        // @Spy ensures caching proxy works if present, mocks handle dependencies
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void get_shouldReturnCurrencyDTO_whenCurrencyExists() {
        /// Given
        CurrencyEntity entity = new CurrencyEntity(TEST_ID, "dolar amerykański", TEST_CURRENCY_CODE, TEST_NO, TEST_EFFECTIVE_DATE, TEST_MID);
        CurrencyDTO expectedDTO = new CurrencyDTO(TEST_ID, "dolar amerykański", TEST_CURRENCY_CODE, TEST_NO, TEST_EFFECTIVE_DATE, TEST_MID);

        when(repository.findByCodeAndEffectiveDate(TEST_CURRENCY_CODE, LocalDate.now())).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(expectedDTO);

        /// When
        CurrencyDTO result = currencyService.get(TEST_CURRENCY_CODE, LocalDate.now());

        /// Then
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals(TEST_CURRENCY_CODE, result.getCode());
        assertEquals("dolar amerykański", result.getCurrency());

        verify(repository).findByCodeAndEffectiveDate(TEST_CURRENCY_CODE, LocalDate.now());
        verify(mapper).toDTO(entity);
    }

    @Test
    void get_shouldFetchFromAPIAndSave_whenCurrencyNotInDatabase() {
        /// Given
        when(repository.findByCodeAndEffectiveDate(TEST_CURRENCY_CODE, LocalDate.now())).thenReturn(Optional.empty());

        CurrencyRate rate = new CurrencyRate(TEST_NO, TEST_EFFECTIVE_DATE, TEST_MID);
        ArrayList<CurrencyRate> rates = new ArrayList<>();
        rates.add(rate);
        CurrencyFetch fetch = new CurrencyFetch("A", "dolar amerykański", TEST_CURRENCY_CODE, rates);

        when(currencyClient.fetchExchangeRate(TEST_CURRENCY_CODE)).thenReturn(fetch);

        CurrencyEntity savedEntity = new CurrencyEntity(TEST_ID, "dolar amerykański", TEST_CURRENCY_CODE, TEST_NO, TEST_EFFECTIVE_DATE, TEST_MID);
        CurrencyDTO savedDTO = new CurrencyDTO(TEST_ID, "dolar amerykański", TEST_CURRENCY_CODE, TEST_NO, TEST_EFFECTIVE_DATE, TEST_MID);

        when(mapper.toEntity(any(CurrencyDTO.class))).thenReturn(savedEntity);
        when(repository.save(any(CurrencyEntity.class))).thenReturn(savedEntity);
        when(mapper.toDTO(savedEntity)).thenReturn(savedDTO);

        /// When
        CurrencyDTO result = currencyService.get(TEST_CURRENCY_CODE, TEST_EFFECTIVE_DATE);

        /// Then
        assertNotNull(result);
        assertEquals(TEST_CURRENCY_CODE, result.getCode());
        assertEquals("dolar amerykański", result.getCurrency());

        verify(repository).findByCodeAndEffectiveDate(TEST_CURRENCY_CODE, LocalDate.now());
        verify(currencyClient).fetchExchangeRate(TEST_CURRENCY_CODE);
        verify(repository).save(any(CurrencyEntity.class));
    }

    @Test
    void delete_shouldDeleteCurrency_whenCurrencyExists() {
        /// Given
        CurrencyEntity entity = new CurrencyEntity(TEST_ID, "dolar amerykański", TEST_CURRENCY_CODE, TEST_NO, TEST_EFFECTIVE_DATE, TEST_MID);
        CurrencyDTO dto = new CurrencyDTO(TEST_ID, "dolar amerykański", TEST_CURRENCY_CODE, TEST_NO, TEST_EFFECTIVE_DATE, TEST_MID);

        when(repository.findByCodeAndEffectiveDate(eq(TEST_CURRENCY_CODE), any(LocalDate.class))).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(dto);
        when(repository.existsById(TEST_ID)).thenReturn(true);
        doNothing().when(repository).deleteById(TEST_ID);

        /// When
        currencyService.delete(TEST_CURRENCY_CODE, TEST_EFFECTIVE_DATE);

        /// Then
        verify(repository).findByCodeAndEffectiveDate(eq(TEST_CURRENCY_CODE), any(LocalDate.class));
        verify(repository).existsById(TEST_ID);
        verify(repository).deleteById(TEST_ID);
    }

    @Test
    void delete_shouldThrowException_whenCurrencyNotFound() {
        /// Given
        String errorMessage = "Currency XXX not found";
        ApplicationException expectedException = new ApplicationException("NOT_FOUND", errorMessage, HttpStatus.NOT_FOUND);

        when(repository.findByCodeAndEffectiveDate("XXX", LocalDate.now())).thenReturn(Optional.empty());
        when(currencyClient.fetchExchangeRate("XXX")).thenThrow(expectedException);

        /// When & Then
        ApplicationException thrown = assertThrows(ApplicationException.class, () -> currencyService.delete("XXX", LocalDate.now()));

        /// Then
        assertEquals(expectedException, thrown);
        verify(repository).findByCodeAndEffectiveDate("XXX", LocalDate.now());
        verify(repository, never()).deleteById(any());
    }

    @Test
    void get_shouldHandleDifferentCurrencyCodes() {
        /// Given
        String eurCode = "EUR";
        String eurNo = "248/A/NBP/2025";
        LocalDate eurEffectiveDate = LocalDate.of(2025, 12, 23);
        double eurMid = 4.2274;

        CurrencyRate eurRate = new CurrencyRate(eurNo, eurEffectiveDate, eurMid);
        CurrencyFetch eurFetch = new CurrencyFetch("A", "Euro", eurCode, new ArrayList<>(List.of(eurRate)));

        CurrencyEntity eurEntity = new CurrencyEntity(2L, "Euro", eurCode, eurNo, eurEffectiveDate, eurMid);
        CurrencyDTO eurDTO = new CurrencyDTO(2L, "Euro", eurCode, eurNo, eurEffectiveDate, eurMid);

        /// When
        when(currencyClient.fetchExchangeRate(eurCode)).thenReturn(eurFetch);
//        when(repository.findByCodeAndEffectiveDate(eurCode, eurEffectiveDate)).thenReturn(Optional.of(eurEntity));
//        when(currencyService.save(eurDTO)).thenReturn(eurDTO);
        when(mapper.toDTO(any())).thenReturn(eurDTO);
        when(mapper.toEntity(any())).thenReturn(eurEntity);
        when(repository.save(any())).thenReturn(eurEntity);

        CurrencyDTO result = currencyService.get(eurCode, eurEffectiveDate);

        /// Then
        assertNotNull(result);
        assertEquals(eurCode, result.getCode());
        assertEquals("Euro", result.getCurrency());

        verify(repository).findByCodeAndEffectiveDate(eurCode, eurEffectiveDate);
    }
}
