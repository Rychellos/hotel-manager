package pl.rychellos.hotel.currencyexchange;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import pl.rychellos.hotel.currencyexchange.contract.CurrencyFetchDTO;
import pl.rychellos.hotel.currencyexchange.contract.CurrencyRateDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NBPApiTest {

    @Mock
    private NBPApi nbpApi;

    @Test
    void fetchExchangeRate_Success() {
        /// Given
        String currencyCode = "usd";
        CurrencyRateDTO rate = new CurrencyRateDTO("064/A/NBP/2016", LocalDate.of(2016, 4, 4), 3.7254);
        CurrencyFetchDTO expectedFetch = new CurrencyFetchDTO("A", "dolar amerykański", "USD", new ArrayList<>(List.of(rate)));

        /// When
        when(nbpApi.getRate(currencyCode)).thenReturn(expectedFetch);

        CurrencyFetchDTO result = nbpApi.getRate(currencyCode);

        /// Then
        assertEquals("USD", result.code());
        assertEquals(1, result.rates().size());
        verify(nbpApi).getRate(currencyCode);
    }

    @Test
    void fetchExchangeRate_NotFound() {
        /// Given
        String currencyCode = "INVALID";

        /// When
        when(nbpApi.getRate(currencyCode))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        /// Then
        assertThrows(HttpClientErrorException.class, () -> {
            nbpApi.getRate(currencyCode);
        });
    }
}