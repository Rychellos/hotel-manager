package pl.rychellos.hotel.currencyExchange;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.rychellos.hotel.currencyExchange.contract.CurrencyFetch;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

public class CurrencyClient implements ICurrencyClient {
    private final ApplicationExceptionFactory applicationExceptionFactory;
    private final LangUtil langUtil;
    protected final ICurrencyUriBuilderProvider uriBuilderProvider;
    protected final RestClient restClient;

    public CurrencyClient(ApplicationExceptionFactory applicationExceptionFactory, LangUtil langUtil, ICurrencyUriBuilderProvider uriBuilderProvider, RestClient restClient) {
        this.applicationExceptionFactory = applicationExceptionFactory;
        this.langUtil = langUtil;
        this.uriBuilderProvider = uriBuilderProvider;
        this.restClient = restClient;
    }

    public CurrencyFetch fetchExchangeRate(String currencyCode) throws ApplicationException {
        String uri = uriBuilderProvider.builder()
            .pathSegment("a")
            .pathSegment(currencyCode)
            .queryParam("format", "json")
            .toUriString();

        try {
            return restClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RestClientException("Currency not found");
                })
                .body(CurrencyFetch.class);
        } catch (Exception e) {
            throw applicationExceptionFactory.resourceNotFound(
                langUtil.getMessage("error.currency.notFound.message").formatted(currencyCode)
            );
        }
    }
}
