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

package be.thibaulthelsmoortel.currencyconverterbot.security.service;

import be.thibaulthelsmoortel.currencyconverterbot.security.service.payload.ApiAuthentication;
import be.thibaulthelsmoortel.currencyconverterbot.security.service.payload.SigninBody;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

/**
 * @author Thibault Helsmoortel
 */
@SpringBootTest
class AuthorizationServiceTest {

    @Autowired
    private AuthorizationService authorizationService;

    @Qualifier("unauthenticatedApiClient")
    @MockBean
    private WebClient unauthenticatedApiClient;

    @MockBean
    private Consumer<HttpHeaders> apiHeaders;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void testFirstTimeAuthorize() {
        RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(RequestBodyUriSpec.class);
        Mockito.when(unauthenticatedApiClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri("/auth/signin")).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.headers(apiHeaders)).thenReturn(requestBodyUriSpec);
        RequestHeadersSpec requestHeadersSpec = Mockito.mock(RequestHeadersSpec.class);
        Mockito.when(requestBodyUriSpec.bodyValue(ArgumentMatchers.any(SigninBody.class))).thenReturn(requestHeadersSpec);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        ApiAuthentication apiAuthentication = new ApiAuthentication();
        apiAuthentication.setAccessToken("access");
        Mockito.when(responseSpec.bodyToMono(ApiAuthentication.class)).thenReturn(Mono.just(apiAuthentication));

        String result = authorizationService.authorize();

        Assertions.assertNotNull(result, "Result must not be null.");
        Assertions.assertEquals(apiAuthentication.getAccessToken(), result, "Response must be correct.");
    }
}