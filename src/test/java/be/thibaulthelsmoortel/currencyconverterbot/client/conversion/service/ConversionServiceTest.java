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

import be.thibaulthelsmoortel.currencyconverterbot.BaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.client.conversion.payload.ConversionRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.conversion.payload.ConversionResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

/**
 * @author Thibault Helsmoortel
 */
class ConversionServiceTest extends BaseTest {

    @Autowired
    private ConversionService conversionService;

    @Qualifier("apiClient")
    @MockBean
    private WebClient apiClient;

    @MockBean
    private Consumer<HttpHeaders> apiHeaders;

    @Captor
    private ArgumentCaptor<Function<UriBuilder, URI>> argumentCaptor;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void shouldGetConversion() {
        int sourceAmount = 1;
        String sourceIsoCode = "EUR";
        String targetIsoCode = "USD";
        int result = 5;
        ConversionResponse conversionResponse = new ConversionResponse();
        conversionResponse.setSourceAmount(sourceAmount);
        conversionResponse.setSourceIsoCode(sourceIsoCode);
        conversionResponse.setTargetIsoCode(targetIsoCode);
        conversionResponse.setResult(result);

        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        Mockito.when(apiClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(argumentCaptor.capture())).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.headers(apiHeaders)).thenReturn(requestHeadersUriSpec);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        Mockito.when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        Mockito.when(responseSpec.bodyToMono(ConversionResponse.class))
                .thenReturn(Mono.just(conversionResponse));

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setSourceAmount(sourceAmount);
        conversionRequest.setSourceIsoCode(sourceIsoCode);
        conversionRequest.setTargetIsoCode(targetIsoCode);

        ConversionResponse response = conversionService.getConversion(conversionRequest);

        URI uri = argumentCaptor.getValue().apply(new DefaultUriBuilderFactory().builder());
        Assertions.assertEquals(
                "/v1/convert",
                uri.getPath(),
                "Called path must be correct.");

        assertParamValueEquals(uri, "sourceAmount", String.valueOf(conversionRequest.getSourceAmount()));
        assertParamValueEquals(uri, "sourceIsoCode", conversionRequest.getSourceIsoCode());
        assertParamValueEquals(uri, "targetIsoCode", conversionRequest.getTargetIsoCode());

        Assertions.assertNotNull(response, "Result must not be null.");
        Assertions.assertEquals(conversionResponse, response, "Response must be correct.");
        Assertions.assertEquals(result, response.getResult(), "Result amount must be correct.");
    }

    private void assertParamValueEquals(URI uri, String paramName, String paramValue) {
        Assertions.assertTrue(URLEncodedUtils.parse(uri, StandardCharsets.UTF_8).stream()
                        .filter(param -> param.getName().equals(paramName))
                        .anyMatch(param -> param.getValue().equals(paramValue)),
                "Could not find matching value for parameter: " + paramName);
    }
}