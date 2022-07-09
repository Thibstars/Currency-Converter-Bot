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

package be.thibaulthelsmoortel.currencyconverterbot.client.health.service;

import be.thibaulthelsmoortel.currencyconverterbot.BaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.client.ClientBaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.client.health.payload.HealthResponse;
import java.net.URI;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

/**
 * @author Thibault Helsmoortel
 */
class HealthServiceTest extends ClientBaseTest {

    @Autowired
    private HealthServiceBean healthService;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void shouldGetHealth() {
        HealthResponse healthResponse = new HealthResponse();
        healthResponse.setStatus("UP");

        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        Mockito.when(getApiClient().get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(getUriFunctionCaptor().capture())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.headers(getApiHeaders())).thenReturn(requestHeadersUriSpec);
        RequestHeadersSpec requestHeadersSpec = Mockito.mock(RequestHeadersSpec.class);
        Mockito.when(requestHeadersUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(HealthResponse.class))
                .thenReturn(Mono.just(healthResponse));

        HealthResponse result = healthService.getHealth();

        assertPathEquals(getUri(), "/actuator/health");

        Assertions.assertNotNull(result, "Result must not be null.");
        Assertions.assertEquals(healthResponse, result, "Response must be correct.");
    }

}