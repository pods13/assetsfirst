package com.topably.assets.findata.xrates.config;

import com.topably.assets.findata.xrates.service.provider.client.FreeCurrencyClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class FreeCurrencyClientConfig {

    @Bean
    public FreeCurrencyClient freeCurrencyClient(WebClient.Builder builder) {
        var exchangeAdapter = WebClientAdapter.create(builder.baseUrl("https://api.freecurrencyapi.com/v1")
            .build());
        return HttpServiceProxyFactory.builder()
            .exchangeAdapter(exchangeAdapter)
            .build()
            .createClient(FreeCurrencyClient.class);
    }
}
