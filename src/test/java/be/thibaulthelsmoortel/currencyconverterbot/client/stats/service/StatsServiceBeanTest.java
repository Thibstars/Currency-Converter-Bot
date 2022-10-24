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

package be.thibaulthelsmoortel.currencyconverterbot.client.stats.service;

import be.thibaulthelsmoortel.currencyconverterbot.client.ClientBaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.client.stats.payload.CurrencyStat;
import be.thibaulthelsmoortel.currencyconverterbot.client.stats.payload.StatsRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.stats.payload.StatsResponse;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

/**
 * @author Thibault Helsmoortel
 */
class StatsServiceBeanTest extends ClientBaseTest {

    @Autowired
    private StatsService statsService;

    @DisplayName("Should get stats")
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void shouldGetStats() {
        String isoCode = "EUR";
        long conversionsWithSource = 5L;
        List<CurrencyStat> mostConversionsWithSource = List.of(
                CurrencyStat.builder()
                        .isoCode("USD")
                        .occurrences(5L)
                        .build()
        );
        long conversionsWithTarget = 3L;
        List<CurrencyStat> mostConversionsWithTarget = List.of(
                CurrencyStat.builder()
                        .isoCode("UYU")
                        .occurrences(2L)
                        .build(),
                CurrencyStat.builder()
                        .isoCode("JPY")
                        .occurrences(1L)
                        .build()
        );

        StatsResponse statsResponse = new StatsResponse();
        statsResponse.setIsoCode(isoCode);
        statsResponse.setConversionsWithSource(conversionsWithSource);
        statsResponse.setMostConversionsWithSource(mostConversionsWithSource);
        statsResponse.setConversionsWithTarget(conversionsWithTarget);
        statsResponse.setMostConversionsWithTarget(mostConversionsWithTarget);

        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        Mockito.when(getApiClient().get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(getUriFunctionCaptor().capture())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.headers(getApiHeaders())).thenReturn(requestHeadersUriSpec);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        Mockito.when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(StatsResponse.class))
                .thenReturn(Mono.just(statsResponse));

        StatsRequest statsRequest = new StatsRequest();
        statsRequest.setIsoCode(isoCode);

        StatsResponse response = statsService.getStats(statsRequest);

        assertPathEquals("/v1/stats");

        assertParamValueEquals("isoCode", statsRequest.getIsoCode());

        Assertions.assertNotNull(response, "Result must not be null.");
        Assertions.assertEquals(statsResponse, response, "Response must be correct.");
        Assertions.assertEquals(conversionsWithSource,
                response.getConversionsWithSource(),
                "Conversions with source must be correct.");
        Assertions.assertNotNull(response.getMostConversionsWithSource(),
                "Conversions with source must not be null.");
        Assertions.assertFalse(response.getMostConversionsWithSource().isEmpty(),
                "Conversions with source must not be empty.");
        Assertions.assertEquals(mostConversionsWithSource,
                response.getMostConversionsWithSource(),
                "Conversions with source must be correct.");
        Assertions.assertEquals(conversionsWithTarget,
                response.getConversionsWithTarget(),
                "Conversions with target must be correct.");
        Assertions.assertNotNull(response.getMostConversionsWithTarget(),
                "Conversions with target must not be null.");
        Assertions.assertFalse(response.getMostConversionsWithTarget().isEmpty(),
                "Conversions with target must not be empty.");
        Assertions.assertEquals(mostConversionsWithTarget,
                response.getMostConversionsWithTarget(),
                "Conversions with target must be correct.");    }
}