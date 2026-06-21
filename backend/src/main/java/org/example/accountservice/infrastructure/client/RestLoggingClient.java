package org.example.accountservice.infrastructure.client;

import org.example.accountservice.domain.client.LoggingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;

@Component
public class RestLoggingClient implements LoggingClient {
    private static final Logger log = LoggerFactory.getLogger(RestLoggingClient.class);
    HttpClientSettings settings = HttpClientSettings.defaults()
            .withConnectTimeout(Duration.ofMillis(100))
            .withReadTimeout(Duration.ofSeconds(2));
    ClientHttpRequestFactory factory = ClientHttpRequestFactoryBuilder.detect()
            .build(settings);

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://example.com")
            .requestFactory(factory)
            .build();

    @Override
    public void logDebitRequest() {
        try {
            restClient.get()
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("[RestLoggingClient] error calling client", e);
        }
    }
}
