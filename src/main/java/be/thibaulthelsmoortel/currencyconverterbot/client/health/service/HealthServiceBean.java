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

import be.thibaulthelsmoortel.currencyconverterbot.client.health.payload.HealthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Thibault Helsmoortel
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class HealthServiceBean implements HealthService {

    private final WebClient apiClient;

    @Override
    public HealthResponse getHealth() {
        log.info("Fetching health.");

        Mono<HealthResponse> healthMono = apiClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/actuator/health")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(HealthResponse.class);

        return healthMono.block();
    }
}
