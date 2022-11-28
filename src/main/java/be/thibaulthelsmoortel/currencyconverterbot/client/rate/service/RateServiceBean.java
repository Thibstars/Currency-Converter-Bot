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

import be.thibaulthelsmoortel.currencyconverterbot.client.rate.payload.RateRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.rate.payload.RateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Thibault Helsmoortel
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class RateServiceBean implements RateService {

    private final WebClient apiClient;

    @Override
    public RateResponse getRate(RateRequest rateRequest) {
        log.info("Fetching rate for request: {}", rateRequest);

        Mono<RateResponse> rateMono = apiClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/rate")
                        .queryParam("baseIsoCode", rateRequest.getBaseIsoCode())
                        .queryParam("targetIsoCode", rateRequest.getTargetIsoCode())
                        .build())
                .retrieve()
                .bodyToMono(RateResponse.class);

        return rateMono.block();
    }
}
