package pl.rychellos.hotel.currencyexchange.configuration;

import java.net.http.HttpClient;
import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;
import pl.rychellos.hotel.currencyexchange.NBPApi;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ImportHttpServices(NBPApi.class)
public class NBPApiConfiguration {
    @Value("${currency.api.nbp}")
    private String nbpBaseUrl;

    private final SslBundles sslBundles;

    public NBPApiConfiguration(SslBundles sslBundles) {
        this.sslBundles = sslBundles;
    }

    @Bean
    public RestClientHttpServiceGroupConfigurer nbpClientConfigurer() {
        return groups -> {
            groups.forEachClient((name, builder) -> {
                builder.baseUrl(nbpBaseUrl);

                SSLContext sslContext = sslBundles.getBundle("my-bundle").createSslContext();

                JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(
                    HttpClient.newBuilder()
                        .sslContext(sslContext)
                        .build()
                );

                builder.requestFactory(factory);
            });
        };
    }
}
