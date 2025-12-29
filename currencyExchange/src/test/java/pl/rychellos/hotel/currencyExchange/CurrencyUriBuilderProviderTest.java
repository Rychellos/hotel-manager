package pl.rychellos.hotel.currencyExchange;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyUriBuilderProviderTest {

    @Test
    void builder_shouldReturnUriComponentsBuilderWithHttpsScheme() {
        // Given
        CurrencyUriBuilderProvider provider = new CurrencyUriBuilderProvider(
            "api.nbp.pl",
            "api/exchangerates/rates"
        );

        // When
        UriComponentsBuilder builder = provider.builder();
        String uri = builder.toUriString();

        // Then
        assertNotNull(builder);
        assertTrue(uri.startsWith("https://"));
    }

    @Test
    void builder_shouldIncludeHostInUri() {
        // Given
        String host = "api.nbp.pl";
        CurrencyUriBuilderProvider provider = new CurrencyUriBuilderProvider(
            host,
            "api/exchangerates/rates"
        );

        // When
        UriComponentsBuilder builder = provider.builder();
        String uri = builder.toUriString();

        // Then
        assertTrue(uri.contains(host));
    }

    @Test
    void builder_shouldIncludePathInUri() {
        // Given
        String path = "api/exchangerates/rates";
        CurrencyUriBuilderProvider provider = new CurrencyUriBuilderProvider(
            "api.nbp.pl",
            path
        );

        // When
        UriComponentsBuilder builder = provider.builder();
        String uri = builder.toUriString();

        // Then
        assertTrue(uri.contains("api/exchangerates/rates"));
    }

    @Test
    void builder_shouldBuildCompleteUri() {
        // Given
        CurrencyUriBuilderProvider provider = new CurrencyUriBuilderProvider(
            "api.nbp.pl",
            "api/exchangerates/rates"
        );

        // When
        String uri = provider.builder().toUriString();

        // Then
        assertEquals("https://api.nbp.pl/api/exchangerates/rates", uri);
    }

    @Test
    void builder_shouldAllowPathSegmentAdditions() {
        // Given
        CurrencyUriBuilderProvider provider = new CurrencyUriBuilderProvider(
            "api.nbp.pl",
            "api/exchangerates/rates"
        );

        // When
        String uri = provider.builder()
            .pathSegment("A")
            .pathSegment("USD")
            .toUriString();

        // Then
        assertEquals("https://api.nbp.pl/api/exchangerates/rates/A/USD", uri);
    }

    @Test
    void builder_shouldAllowQueryParameters() {
        // Given
        CurrencyUriBuilderProvider provider = new CurrencyUriBuilderProvider(
            "api.nbp.pl",
            "api/exchangerates/rates"
        );

        // When
        String uri = provider.builder()
            .pathSegment("A")
            .pathSegment("USD")
            .queryParam("format", "json")
            .toUriString();

        // Then
        assertEquals("https://api.nbp.pl/api/exchangerates/rates/A/USD?format=json", uri);
    }

    @Test
    void host_shouldReturnConfiguredHost() {
        // Given
        String expectedHost = "api.nbp.pl";
        CurrencyUriBuilderProvider provider = new CurrencyUriBuilderProvider(
            expectedHost,
            "api/exchangerates/rates"
        );

        // When
        String host = provider.host();

        // Then
        assertEquals(expectedHost, host);
    }

    @Test
    void path_shouldReturnConfiguredPath() {
        // Given
        String expectedPath = "api/exchangerates/rates";
        CurrencyUriBuilderProvider provider = new CurrencyUriBuilderProvider(
            "api.nbp.pl",
            expectedPath
        );

        // When
        String path = provider.path();

        // Then
        assertEquals(expectedPath, path);
    }

    @Test
    void builder_shouldHandleDifferentHostsAndPaths() {
        // Given
        CurrencyUriBuilderProvider provider1 = new CurrencyUriBuilderProvider(
            "api.example.com",
            "v1/currency"
        );
        CurrencyUriBuilderProvider provider2 = new CurrencyUriBuilderProvider(
            "currency.api.com",
            "exchange/rates"
        );

        // When
        String uri1 = provider1.builder().toUriString();
        String uri2 = provider2.builder().toUriString();

        // Then
        assertEquals("https://api.example.com/v1/currency", uri1);
        assertEquals("https://currency.api.com/exchange/rates", uri2);
    }

    @Test
    void builder_shouldCreateNewBuilderEachTime() {
        // Given
        CurrencyUriBuilderProvider provider = new CurrencyUriBuilderProvider(
            "api.nbp.pl",
            "api/exchangerates/rates"
        );

        // When
        UriComponentsBuilder builder1 = provider.builder();
        UriComponentsBuilder builder2 = provider.builder();

        // Then
        assertNotSame(builder1, builder2, "Each call to builder() should return a new instance");
    }
}
