/*
 * Copyright (c) 2022 Thibault Helsmoortel.
 *
 *  This file is part of Currency Converter Bot.
 *
 *  Currency Converter Bot is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Currency Converter Bot is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Currency Converter Bot.  If not, see <https://www.gnu.org/licenses/>.
 */

package be.thibaulthelsmoortel.currencyconverterbot.config;

import be.thibaulthelsmoortel.currencyconverterbot.security.service.AuthorizationService;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApiClientConfig extends WebSecurityConfigurerAdapter {

    @Value("${api.base.url}")
    private String apiBaseUrl;

    private final AuthorizationService authorizationService;

    @Bean
    public WebClient apiClient() {
        return WebClient.builder()
                .defaultHeaders(apiHeaders())
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        clientRequest -> Mono.just(ClientRequest.from(clientRequest)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authorizationService.authorize())
                                .build())))
                .baseUrl(apiBaseUrl)
                .build();
    }

    @Bean
    public Consumer<HttpHeaders> apiHeaders() {
        return httpHeaders -> httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security.csrf().disable().authorizeRequests().anyRequest().permitAll();
    }

}
