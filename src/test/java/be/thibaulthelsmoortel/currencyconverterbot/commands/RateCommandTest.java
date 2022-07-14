/*
 * Copyright (c) 2021 Thibault Helsmoortel.
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

package be.thibaulthelsmoortel.currencyconverterbot.commands;

import be.thibaulthelsmoortel.currencyconverterbot.client.rate.payload.RateRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.rate.payload.RateResponse;
import be.thibaulthelsmoortel.currencyconverterbot.client.rate.service.RateService;
import java.math.BigDecimal;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * @author Thibault Helsmoortel
 */
class RateCommandTest extends CommandBaseTest {

    @InjectMocks
    private RateCommand rateCommand;

    @Mock
    private RateService rateService;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        rateCommand.setEvent(messageReceivedEvent);
        rateCommand.setBaseCurrencyIsoCode("EUR");
    }

    @DisplayName("Should send rate message.")
    @Test
    void shouldSendRateMessage() {
        String isoCode = "USD";
        rateCommand.setIsoCode(isoCode);

        RateRequest rateRequest = new RateRequest();
        rateRequest.setBaseIsoCode("EUR");
        rateRequest.setTargetIsoCode(isoCode);

        RateResponse response = new RateResponse();
        response.setTargetIsoCode(isoCode);
        response.setResult(BigDecimal.TEN);
        Mockito.when(rateService.getRate(rateRequest)).thenReturn(response);

        String message = rateCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertTrue(message.contains(isoCode), "Message should contain USD.");
        Assertions.assertTrue(message.contains(rateRequest.getBaseIsoCode()), "Message should contain base iso code.");
        Assertions.assertTrue(message.contains(response.getResult().toString()), "Message should contain result.");
        verifyOneMessageSent(message);
    }

    @DisplayName("Should send error message.")
    @Test
    void shouldSendErrorMessage() {
        String isoCode = "KAR";
        rateCommand.setIsoCode(isoCode);
        String message = rateCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertEquals(
                "Unable to perform the rate request. Please verify the input parameters and try again. If the issue persists, please make sure to report the issue via the 'issue' command.",
                message, "Message should match.");
        verifyOneMessageSent(message);
    }

    @DisplayName("Should handle WebClientResponseException.")
    @Test
    void shouldHandleWebClientResponseException() {
        rateCommand.setIsoCode("EUR");

        RateRequest rateRequest = new RateRequest();
        rateRequest.setBaseIsoCode("EUR");

        Mockito.when(rateService.getRate(rateRequest)).thenThrow(WebClientResponseException.class);

        Mockito.when(messageChannel.sendMessageEmbeds(ArgumentMatchers.any(MessageEmbed.class))).thenReturn(Mockito.mock(
                MessageAction.class));

        String message = rateCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertEquals(RateCommand.ERROR_MESSAGE, message, "Message should be correct.");
        verifyOneMessageSent(message);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        rateCommand.setIsoCode("CAD");

        verifyDoNotProcessEvent(rateCommand, Mockito.mock(Event.class));
    }

    @AfterEach
    void tearDown() {
        rateCommand.setIsoCode(null);
    }
}
