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

import be.thibaulthelsmoortel.currencyconverterbot.client.conversion.payload.ConversionRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.conversion.payload.ConversionResponse;
import be.thibaulthelsmoortel.currencyconverterbot.client.conversion.service.ConversionService;
import java.math.BigDecimal;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.apache.commons.lang3.StringUtils;
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
class ConvertCommandTest extends CommandBaseTest {

    @InjectMocks
    private ConvertCommand convertCommand;

    @Mock
    private ConversionService conversionService;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        convertCommand.setEvent(slashCommandInteractionEvent);
    }

    @DisplayName("Should send convert message.")
    @Test
    void shouldSendConvertMessage() {
        BigDecimal sourceAmount = BigDecimal.ONE;
        String usdIso = "USD";
        String eurIso = "EUR";

        convertCommand.setSourceAmount(sourceAmount);
        convertCommand.setSourceIsoCode(usdIso);
        convertCommand.setTargetIsoCode(eurIso);

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setSourceAmount(sourceAmount);
        conversionRequest.setSourceIsoCode(usdIso);
        conversionRequest.setTargetIsoCode(eurIso);

        ConversionResponse conversionResponse = new ConversionResponse();
        conversionResponse.setResult(BigDecimal.valueOf(3.33));
        Mockito.when(conversionService.getConversion(conversionRequest)).thenReturn(conversionResponse);

        String message = convertCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertTrue(message.contains(eurIso), "Message should contain EUR.");
        Assertions.assertTrue(message.contains(usdIso), "Message should contain USD.");
        Assertions.assertTrue(message.contains(String.valueOf(conversionResponse.getResult())),
                "Message should contain result.");
        verifyOneMessageReplied(message);
    }

    @DisplayName("Should send error message.")
    @Test
    void shouldSendErrorMessage() {
        BigDecimal sourceAmount = BigDecimal.valueOf(6);
        String usdIso = "USD";
        String unrecognizedIsoCode = "KAR";

        convertCommand.setSourceAmount(sourceAmount);
        convertCommand.setSourceIsoCode(usdIso);
        convertCommand.setTargetIsoCode(unrecognizedIsoCode);

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setSourceAmount(sourceAmount);
        conversionRequest.setSourceIsoCode(usdIso);
        conversionRequest.setTargetIsoCode(unrecognizedIsoCode);

        Mockito.when(conversionService.getConversion(conversionRequest)).thenReturn(null);

        String message = convertCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertEquals(
                "Unable to perform the conversion request. Please verify the input parameters and try again. If the issue persists, please make sure to report the issue via the 'issue' command.",
                message, "Message should match.");
        verifyOneMessageReplied(message);
    }

    @DisplayName("Should handle WebClientResponseException.")
    @Test
    void shouldHandleWebClientResponseException() {
        BigDecimal sourceAmount = BigDecimal.ONE;
        String usdIso = "USD";
        String eurIso = "EUR";

        convertCommand.setSourceAmount(sourceAmount);
        convertCommand.setSourceIsoCode(usdIso);
        convertCommand.setTargetIsoCode(eurIso);

        ConversionRequest conversionRequest = new ConversionRequest();
        conversionRequest.setSourceAmount(sourceAmount);
        conversionRequest.setSourceIsoCode(usdIso);
        conversionRequest.setTargetIsoCode(eurIso);

        Mockito.when(conversionService.getConversion(conversionRequest)).thenThrow(WebClientResponseException.class);

        Mockito.when(messageChannelUnion.sendMessageEmbeds(ArgumentMatchers.any(MessageEmbed.class)))
                .thenReturn(Mockito.mock(MessageCreateAction.class));

        String message = convertCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertEquals(ConvertCommand.ERROR_MESSAGE, message, "Message should be correct.");
        verifyOneMessageReplied(message);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        convertCommand.setSourceAmount(BigDecimal.ONE);
        convertCommand.setSourceIsoCode("EUR");
        convertCommand.setTargetIsoCode("CAD");

        verifyDoNotProcessEvent(convertCommand, Mockito.mock(Event.class));
    }

}
