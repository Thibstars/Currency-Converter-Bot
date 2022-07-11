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

package be.thibaulthelsmoortel.currencyconverterbot.client.rates.service;

import be.thibaulthelsmoortel.currencyconverterbot.client.ClientBaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.client.rate.payload.RateResponse;
import be.thibaulthelsmoortel.currencyconverterbot.client.rates.payload.RatesRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.rates.payload.RatesResponse;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

/**
 * @author Thibault Helsmoortel
 */
class RatesServiceBeanTest extends ClientBaseTest {

    @Autowired
    private RatesService ratesService;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void shouldGetRates() {
        String baseIsoCode = "EUR";
        String targetIsoCode = "USD";
        BigDecimal result = BigDecimal.valueOf(1.17);

        RatesResponse ratesResponse = new RatesResponse();
        ratesResponse.setBaseIsoCode(baseIsoCode);
        RateResponse rateResponse = new RateResponse();
        rateResponse.setBaseIsoCode(baseIsoCode);
        rateResponse.setTargetIsoCode(targetIsoCode);
        rateResponse.setResult(result);
        ratesResponse.setRates(Set.of(rateResponse));

        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        Mockito.when(getApiClient().get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(getUriFunctionCaptor().capture())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.headers(getApiHeaders())).thenReturn(requestHeadersUriSpec);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        Mockito.when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(RatesResponse.class))
                .thenReturn(Mono.just(ratesResponse));

        RatesRequest ratesRequest = new RatesRequest();
        ratesRequest.setBaseIsoCode(baseIsoCode);

        RatesResponse response = ratesService.getRates(ratesRequest);

        assertPathEquals("/v1/rates");

        assertParamValueEquals("baseIsoCode", ratesRequest.getBaseIsoCode());

        Assertions.assertNotNull(response, "Result must not be null.");
        Assertions.assertEquals(ratesResponse, response, "Response must be correct.");
        Assertions.assertNotNull(response.getRates(), "Rates must not be null.");
        Assertions.assertFalse(response.getRates().isEmpty(), "Rates must not be empty.");
        Assertions.assertEquals(result, response.getRates().stream().findFirst().orElseThrow().getResult(), "Result amount must be correct.");
    }
}