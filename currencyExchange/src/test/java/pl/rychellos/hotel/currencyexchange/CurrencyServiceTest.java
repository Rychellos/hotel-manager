package pl.rychellos.hotel.currencyexchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import pl.rychellos.hotel.currencyexchange.contract.CurrencyFetchDTO;
import pl.rychellos.hotel.currencyexchange.contract.CurrencyRateDTO;
import pl.rychellos.hotel.currencyexchange.dto.CurrencyDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CurrencyServiceTest {
    @Mock
    private LangUtil langUtil;

    @Mock
    private CurrencyMapper mapper;

    @Mock
    private CurrencyRepository repository;

    @Mock
    private ApplicationExceptionFactory exceptionFactory;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ICurrencyClient currencyClient;

    private CurrencyService currencyService;

    private static final String TEST_CURRENCY_CODE = "USD";
    private static final Long TEST_ID = 1L;
    private static final String TEST_NO = "248/A/NBP/2025";
    private static final LocalDate TEST_EFFECTIVE_DATE = LocalDate.of(2025, 12, 23);
    private static final Double TEST_MID = 3.5848;

    @BeforeEach
    void setUp() {
        currencyService = new CurrencyService(
            langUtil,
            mapper,
            repository,
            exceptionFactory,
            objectMapper,
            currencyClient
        );
    }

    @Test
    void get_shouldReturnCurrencyDTO_whenCurrencyExists() {
        /// Given
        CurrencyEntity entity = new CurrencyEntity(
            TEST_ID, UUID.randomUUID(),
            "dolar amerykański",
            TEST_CURRENCY_CODE,
            TEST_NO,
            TEST_EFFECTIVE_DATE,
            TEST_MID
        );
        CurrencyDTO expectedDTO = new CurrencyDTO(
            TEST_ID,
            UUID.randomUUID(), "dolar amerykański",
            TEST_CURRENCY_CODE,
            TEST_NO,
            TEST_EFFECTIVE_DATE,
            TEST_MID
        );

        when(repository.findByCodeAndEffectiveDate(TEST_CURRENCY_CODE, LocalDate.now()))
            .thenReturn(Optional.of(entity));
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
        when(repository.findByCodeAndEffectiveDate(TEST_CURRENCY_CODE, TEST_EFFECTIVE_DATE))
            .thenReturn(Optional.empty());

        CurrencyRateDTO rate = new CurrencyRateDTO(TEST_NO, TEST_EFFECTIVE_DATE, TEST_MID);
        ArrayList<CurrencyRateDTO> rates = new ArrayList<>();
        rates.add(rate);
        CurrencyFetchDTO fetch = new CurrencyFetchDTO("A", "dolar amerykański", TEST_CURRENCY_CODE, rates);

        CurrencyEntity savedEntity = new CurrencyEntity(
            TEST_ID,
            UUID.randomUUID(),
            "dolar amerykański",
            TEST_CURRENCY_CODE,
            TEST_NO,
            TEST_EFFECTIVE_DATE,
            TEST_MID
        );
        CurrencyDTO savedDTO = new CurrencyDTO(
            TEST_ID,
            UUID.randomUUID(),
            "dolar amerykański",
            TEST_CURRENCY_CODE,
            TEST_NO,
            TEST_EFFECTIVE_DATE,
            TEST_MID
        );

        when(currencyClient.getRate(TEST_CURRENCY_CODE)).thenReturn(fetch);

        when(mapper.toEntity(any(CurrencyDTO.class))).thenReturn(savedEntity);
        when(repository.save(any(CurrencyEntity.class))).thenReturn(savedEntity);
        when(mapper.toDTO(any(CurrencyEntity.class))).thenReturn(savedDTO);

        /// When
        CurrencyDTO result = currencyService.get(TEST_CURRENCY_CODE, TEST_EFFECTIVE_DATE);

        /// Then
        assertNotNull(result);
        assertEquals(TEST_CURRENCY_CODE, result.getCode());
        assertEquals("dolar amerykański", result.getCurrency());

        verify(repository).findByCodeAndEffectiveDate(TEST_CURRENCY_CODE, TEST_EFFECTIVE_DATE);
        verify(currencyClient).getRate(TEST_CURRENCY_CODE);
        verify(repository).save(any(CurrencyEntity.class));
    }

    @Test
    void delete_shouldDeleteCurrency_whenCurrencyExists() {
        /// Given
        CurrencyEntity entity = new CurrencyEntity(
            TEST_ID,
            UUID.randomUUID(),
            "dolar amerykański",
            TEST_CURRENCY_CODE,
            TEST_NO,
            TEST_EFFECTIVE_DATE,
            TEST_MID
        );
        CurrencyDTO dto = new CurrencyDTO(
            TEST_ID,
            UUID.randomUUID(),
            "dolar amerykański",
            TEST_CURRENCY_CODE,
            TEST_NO,
            TEST_EFFECTIVE_DATE,
            TEST_MID
        );

        /// When
        when(repository.findByCodeAndEffectiveDate(eq(TEST_CURRENCY_CODE), any(LocalDate.class)))
            .thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(dto);
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.existsById(TEST_ID)).thenReturn(true);

        doNothing().when(repository).deleteById(TEST_ID);

        currencyService.delete(TEST_CURRENCY_CODE, TEST_EFFECTIVE_DATE);

        /// Then
        verify(repository).findByCodeAndEffectiveDate(eq(TEST_CURRENCY_CODE), any(LocalDate.class));
        verify(repository).existsById(TEST_ID);
    }

    @Test
    void delete_shouldThrowException_whenCurrencyNotFound() {
        /// Given
        String errorMessage = "Currency XXX not found";
        ApplicationException expectedException = new ApplicationException("NOT_FOUND", errorMessage, HttpStatus.NOT_FOUND);

        when(repository.findByCodeAndEffectiveDate("XXX", LocalDate.now())).thenReturn(Optional.empty());
        when(currencyClient.getRate("XXX")).thenThrow(expectedException);

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

        CurrencyRateDTO eurRate = new CurrencyRateDTO(eurNo, eurEffectiveDate, eurMid);
        CurrencyFetchDTO eurFetch = new CurrencyFetchDTO("A", "Euro", eurCode, new ArrayList<>(List.of(eurRate)));

        CurrencyEntity eurEntity = new CurrencyEntity(
            2L,
            UUID.randomUUID(),
            "Euro",
            eurCode,
            eurNo,
            eurEffectiveDate,
            eurMid
        );
        CurrencyDTO eurDTO = new CurrencyDTO(
            2L,
            UUID.randomUUID(),
            "Euro",
            eurCode,
            eurNo,
            eurEffectiveDate,
            eurMid
        );

        /// When
        when(repository.findByCodeAndEffectiveDate(eurCode, eurEffectiveDate)).thenReturn(Optional.empty());
        when(currencyClient.getRate(eurCode)).thenReturn(eurFetch);
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
