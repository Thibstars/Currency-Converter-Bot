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
import be.thibaulthelsmoortel.currencyconverterbot.security.service.payload.RefreshTokenBody;
import be.thibaulthelsmoortel.currencyconverterbot.security.service.payload.SigninBody;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
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

    @Test
    void testFirstTimeAuthorize() {
        ApiAuthentication apiAuthentication = mockFirstTimeAuthentication();

        String result = authorizationService.authorize();

        Assertions.assertNotNull(result, "Result must not be null.");
        Assertions.assertEquals(apiAuthentication.getAccessToken(), result, "Response must be correct.");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @NotNull
    private ApiAuthentication mockFirstTimeAuthentication() {
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

        return apiAuthentication;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void testStillSignedIn() {
        mockFirstTimeAuthentication();
        String firstToken = authorizationService.authorize();

        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        Mockito.when(unauthenticatedApiClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri("/roles/client")).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.headers(apiHeaders)).thenReturn(requestHeadersUriSpec);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        Mockito.when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Client Content."));

        String secondToken = authorizationService.authorize();

        Assertions.assertEquals(firstToken, secondToken, "Token must not have changed.");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void testRefreshToken() {
        mockFirstTimeAuthentication();
        String firstToken = authorizationService.authorize();

        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        Mockito.when(unauthenticatedApiClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri("/roles/client")).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.headers(apiHeaders)).thenReturn(requestHeadersUriSpec);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        Mockito.when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(String.class)).thenThrow(RuntimeException.class);

        RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(RequestBodyUriSpec.class);
        Mockito.when(unauthenticatedApiClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri("/auth/refreshtoken")).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.headers(apiHeaders)).thenReturn(requestBodyUriSpec);
        RequestHeadersSpec requestHeadersSpec = Mockito.mock(RequestHeadersSpec.class);
        Mockito.when(requestBodyUriSpec.bodyValue(ArgumentMatchers.any(RefreshTokenBody.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        ApiAuthentication apiAuthentication = new ApiAuthentication();
        apiAuthentication.setAccessToken("refreshed");
        Mockito.when(responseSpec.bodyToMono(ApiAuthentication.class)).thenReturn(Mono.just(apiAuthentication));

        String secondToken = authorizationService.authorize();

        Assertions.assertNotEquals(firstToken, secondToken, "Token must have been refreshed.");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void testSignBackIn() {
        mockFirstTimeAuthentication();
        String firstToken = authorizationService.authorize();

        RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
        Mockito.when(unauthenticatedApiClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri("/roles/client")).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.headers(apiHeaders)).thenReturn(requestHeadersUriSpec);
        ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);
        Mockito.when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(String.class)).thenThrow(RuntimeException.class);

        RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(RequestBodyUriSpec.class);
        Mockito.when(unauthenticatedApiClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri("/auth/refreshtoken")).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.headers(apiHeaders)).thenReturn(requestBodyUriSpec);
        RequestHeadersSpec requestHeadersSpec = Mockito.mock(RequestHeadersSpec.class);
        Mockito.when(requestBodyUriSpec.bodyValue(ArgumentMatchers.any(RefreshTokenBody.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(ApiAuthentication.class)).thenThrow(RuntimeException.class);

        Mockito.when(unauthenticatedApiClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri("/auth/signin")).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.headers(apiHeaders)).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.bodyValue(ArgumentMatchers.any(SigninBody.class))).thenReturn(requestHeadersSpec);
        ResponseSpec responseSpecNewSignIn = Mockito.mock(ResponseSpec.class);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpecNewSignIn);
        ApiAuthentication apiAuthentication = new ApiAuthentication();
        apiAuthentication.setAccessToken("newSignIn");
        Mockito.when(responseSpecNewSignIn.bodyToMono(ApiAuthentication.class)).thenReturn(Mono.just(apiAuthentication));

        String secondToken = authorizationService.authorize();

        Assertions.assertNotEquals(firstToken, secondToken, "Token must have changed after having signed in again.");
    }
}