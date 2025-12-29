package pl.rychellos.hotel.currencyExchange;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import pl.rychellos.hotel.currencyExchange.contract.CurrencyFetch;
import pl.rychellos.hotel.currencyExchange.contract.CurrencyRate;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyClientTest {
    @Mock
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Mock
    private LangUtil langUtil;

    @Mock
    private ICurrencyUriBuilderProvider uriBuilderProvider;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    @InjectMocks
    private CurrencyClient currencyClient;

    private final String EXPECTED_BASE_URI = "https://api.nbp.pl/api/exchangerates/rates/";

    @Test
    void fetchExchangeRate_Success() {
        // Given
        String currencyCode = "usd";
        String expectedFinalUri = "https://api.nbp.pl/api/exchangerates/rates/a/usd?format=json";
        ArrayList<CurrencyRate> rates = new ArrayList<>();
        rates.add(new CurrencyRate("064/A/NBP/2016", LocalDate.of(2016, 4, 4), 3.7254));
        CurrencyFetch expectedFetch = new CurrencyFetch("A", "dolar amerykański", "USD", rates);

        // When
        when(uriBuilderProvider.builder()).thenReturn(UriComponentsBuilder.fromUriString(EXPECTED_BASE_URI));
        when(restClient.get()
            .uri(expectedFinalUri)
            .retrieve()
            .onStatus(any(), any())
            .body(CurrencyFetch.class)
        ).thenReturn(expectedFetch);

        CurrencyFetch result = currencyClient.fetchExchangeRate(currencyCode);

        // Then
        assertEquals(expectedFetch, result);
        verify(uriBuilderProvider).builder();
    }

    @Test
    void fetchExchangeRate_NotFound() {
        // Arrange
        String currencyCode = "INVALID";
        String expectedFinalUri = "https://api.nbp.pl/api/exchangerates/rates/a/INVALID?format=json";
        when(uriBuilderProvider.builder()).thenReturn(UriComponentsBuilder.fromUriString(EXPECTED_BASE_URI));

        RestClientException restEx = new RestClientException("Currency not found");
        when(restClient.get()
            .uri(expectedFinalUri)
            .retrieve()
            .onStatus(any(), any())
            .body(CurrencyFetch.class)
        ).thenThrow(restEx);

        when(langUtil.getMessage("error.currency.notFound.message")).thenReturn("Currency %s not found");
        when(applicationExceptionFactory.resourceNotFound("Currency INVALID not found"))
            .thenThrow(new ApplicationException("Not found", "Currency INVALID not found", HttpStatus.NOT_FOUND)); // Assume exception type

        // Act & Assert
        assertThrows(ApplicationException.class, () -> currencyClient.fetchExchangeRate(currencyCode));
        verify(applicationExceptionFactory).resourceNotFound(anyString());
    }
}