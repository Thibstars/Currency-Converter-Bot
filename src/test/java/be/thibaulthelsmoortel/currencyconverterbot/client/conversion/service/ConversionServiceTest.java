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

package be.thibaulthelsmoortel.currencyconverterbot.client.conversion.service;

import be.thibaulthelsmoortel.currencyconverterbot.client.ClientBaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.client.conversion.payload.ConversionRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.conversion.payload.ConversionResponse;
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
class ConversionServiceTest extends ClientBaseTest {

    @Autowired
    private ConversionService conversionService;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void shouldGetConversion() {
        BigDecimal sourceAmount = BigDecimal.ONE;
        String sourceIsoCode = "EUR";
        String targetIsoCode = "USD";
        BigDecimal result = BigDecimal.valueOf(5);
        ConversionResponse conversionResponse = new ConversionResponse();
        conversionResponse.setSourceAmount(sourceAmount);
        conversionResponse.setSourceIsoCode(sourceIsoCode);
        conversionResponse.setTargetIsoCode(targetIsoCode);
        conversionResponse.setResult(result);

        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        Mockito.when(getApiClient().get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(getUriFunctionCaptor().capture())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.headers(getApiHeaders())).thenReturn(requestHeadersUriSpec);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        Mockito.when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(ConversionResponse.class))
                .thenReturn(Mono.just(conversionResponse));

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setSourceAmount(sourceAmount);
        conversionRequest.setSourceIsoCode(sourceIsoCode);
        conversionRequest.setTargetIsoCode(targetIsoCode);

        ConversionResponse response = conversionService.getConversion(conversionRequest);

        assertPathEquals("/v1/convert");

        assertParamValueEquals("sourceAmount", String.valueOf(conversionRequest.getSourceAmount()));
        assertParamValueEquals("sourceIsoCode", conversionRequest.getSourceIsoCode());
        assertParamValueEquals("targetIsoCode", conversionRequest.getTargetIsoCode());

        Assertions.assertNotNull(response, "Result must not be null.");
        Assertions.assertEquals(conversionResponse, response, "Response must be correct.");
        Assertions.assertEquals(result, response.getResult(), "Result amount must be correct.");
    }
}