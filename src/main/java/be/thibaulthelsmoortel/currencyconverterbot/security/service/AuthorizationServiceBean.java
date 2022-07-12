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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Thibault Helsmoortel
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationServiceBean implements AuthorizationService {

    @Value("${api.base.url}")
    private String apiBaseUrl;

    @Value("${api.auth.username}")
    private String username;

    @Value("${api.auth.password}")
    private String password;

    @Qualifier("unauthenticatedApiClient")
    private final WebClient unauthenticatedApiClient;

    private ApiAuthentication apiAuthentication;

    @Override
    public String authorize() {
        if (apiAuthentication == null || StringUtils.isBlank(apiAuthentication.getAccessToken())) {
            // First time sign in
            this.apiAuthentication = signIn();
        } else {
            try {
                // Check if still signed in
                testAccess();
            } catch (Exception e) {
                // Refresh access or sign back in if refreshToken expired
                try {
                    this.apiAuthentication = refreshToken(apiAuthentication.getRefreshToken());
                } catch (Exception ex) {
                    this.apiAuthentication = signIn();
                }
            }
        }

        return apiAuthentication == null ? "" : apiAuthentication.getAccessToken();
    }

    private ApiAuthentication signIn() {
        log.info("Signing into API.");

        SigninBody body = new SigninBody();
        body.setUsername(username);
        body.setPassword(password);

        Mono<ApiAuthentication> apiAuthenticationMono = unauthenticatedApiClient
                .post()
                .uri("/auth/signin")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(ApiAuthentication.class);

        return apiAuthenticationMono.block();
    }

    private ApiAuthentication refreshToken(String refreshToken) {
        log.info("Refreshing API authentication.");

        RefreshTokenBody body = new RefreshTokenBody();
        body.setRefreshToken(refreshToken);
        Mono<ApiAuthentication> apiAuthenticationMono = unauthenticatedApiClient
                .post()
                .uri("/auth/refreshtoken")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(ApiAuthentication.class);

        return apiAuthenticationMono.block();
    }

    private void testAccess() {
        WebClient client = WebClient.builder()
                .defaultHeaders(
                        httpHeaders -> httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + apiAuthentication.getAccessToken()))
                .baseUrl(apiBaseUrl)
                .build();

        client.get()
                .uri("/roles/client")
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals,
                        response -> response.bodyToMono(String.class).map(Exception::new))
                .onStatus(HttpStatus.FORBIDDEN::equals,
                        response -> response.bodyToMono(String.class).map(Exception::new))
                .bodyToMono(String.class)
                .block();
    }
}
