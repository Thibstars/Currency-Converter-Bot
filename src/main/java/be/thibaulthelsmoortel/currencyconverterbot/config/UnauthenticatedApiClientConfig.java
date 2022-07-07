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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Separate config for the unauthenticated client, which also avoids cyclic dependencies between {@link ApiClientConfig} and {@link be.thibaulthelsmoortel.currencyconverterbot.security.service.AuthorizationService}.
 *
 * @author Thibault Helsmoortel
 */
@Configuration
public class UnauthenticatedApiClientConfig {

    @Value("${api.base.url}")
    private String apiBaseUrl;

    @Bean
    public WebClient unauthenticatedApiClient() {
        return WebClient.builder()
                .defaultHeaders(httpHeaders -> httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .baseUrl(apiBaseUrl)
                .build();
    }

}
