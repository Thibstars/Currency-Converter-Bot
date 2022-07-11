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

package be.thibaulthelsmoortel.currencyconverterbot.client.rate.service;

import be.thibaulthelsmoortel.currencyconverterbot.client.ClientBaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.client.rate.payload.RateRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.rate.payload.RateResponse;
import java.math.BigDecimal;
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
class RateServiceBeanTest extends ClientBaseTest {

    @Autowired
    private RateService rateService;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void shouldGetRate() {
        String baseIsoCode = "EUR";
        String targetIsoCode = "USD";
        BigDecimal result = BigDecimal.valueOf(1.17);

        RateResponse rateResponse = new RateResponse();
        rateResponse.setBaseIsoCode(baseIsoCode);
        rateResponse.setTargetIsoCode(targetIsoCode);
        rateResponse.setResult(result);

        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        Mockito.when(getApiClient().get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(getUriFunctionCaptor().capture())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.headers(getApiHeaders())).thenReturn(requestHeadersUriSpec);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        Mockito.when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(RateResponse.class))
                .thenReturn(Mono.just(rateResponse));

        RateRequest rateRequest = new RateRequest();
        rateRequest.setBaseIsoCode(baseIsoCode);
        rateRequest.setTargetIsoCode(targetIsoCode);

        RateResponse response = rateService.getRate(rateRequest);

        assertPathEquals("/v1/rate");

        assertParamValueEquals("baseIsoCode", rateRequest.getBaseIsoCode());
        assertParamValueEquals("targetIsoCode", rateRequest.getTargetIsoCode());

        Assertions.assertNotNull(response, "Result must not be null.");
        Assertions.assertEquals(rateResponse, response, "Response must be correct.");
        Assertions.assertEquals(result, response.getResult(), "Result amount must be correct.");
    }
}