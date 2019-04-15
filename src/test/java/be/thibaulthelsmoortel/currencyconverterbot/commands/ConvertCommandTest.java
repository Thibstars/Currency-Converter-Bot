/*
 * Copyright (c) 2019 Thibault Helsmoortel.
 *
 * This file is part of Currency Converter Bot.
 *
 * Currency Converter Bot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Currency Converter Bot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Currency Converter Bot.  If not, see <https://www.gnu.org/licenses/>.
 */

package be.thibaulthelsmoortel.currencyconverterbot.commands;

import static org.mockito.Mockito.when;

import be.thibaulthelsmoortel.currencyconverterbot.api.model.Currency;
import be.thibaulthelsmoortel.currencyconverterbot.api.model.Rate;
import be.thibaulthelsmoortel.currencyconverterbot.api.parsers.RatesParser;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * @author Thibault Helsmoortel
 */
class ConvertCommandTest extends CommandBaseTest {

    private ConvertCommand convertCommand;

    @MockBean
    private RatesParser ratesParser;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        this.convertCommand = new ConvertCommand(ratesParser);
        convertCommand.setEvent(messageReceivedEvent);
    }

    @DisplayName("Should send convert message.")
    @Test
    void shouldSendConvertMessage() {
        String usdIso = "USD";
        Rate usdRate = createRate(usdIso, new BigDecimal("0.7532"));
        when(ratesParser.parse(usdIso)).thenReturn(usdRate);

        String eurIso = "EUR";
        Rate eurRate = createRate(eurIso, BigDecimal.ONE);
        when(ratesParser.parse(eurIso)).thenReturn(eurRate);

        convertCommand.setSourceAmount(1);
        convertCommand.setSourceIsoCode(usdIso);
        convertCommand.setTargetIsoCode(eurIso);

        String message = (String) convertCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertTrue(message.contains(eurIso), "Message should contain USD.");
        Assertions.assertTrue(message.contains(usdRate.getValue().toPlainString()), "Message should contain rate.");
        verifyOneMessageSent(message);
    }

    @DisplayName("Should send input not recognized message.")
    @Test
    void shouldSendInputNotRecognizedMessage() {
        String usdIso = "USD";
        Rate usdRate = createRate(usdIso, new BigDecimal("0.7532"));
        when(ratesParser.parse(usdIso)).thenReturn(usdRate);

        String eurIso = "EUR";
        Rate eurRate = createRate(eurIso, BigDecimal.ONE);
        when(ratesParser.parse(eurIso)).thenReturn(eurRate);

        String unrecognizedIsoCode = "Karman";
        when(ratesParser.parse(unrecognizedIsoCode)).thenThrow(NoSuchElementException.class);

        convertCommand.setSourceAmount(6);
        convertCommand.setSourceIsoCode(usdIso);
        convertCommand.setTargetIsoCode(unrecognizedIsoCode);

        String message = (String) convertCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertEquals("Input parameters not recognized.", message, "Message should match.");
        verifyOneMessageSent(message);
    }

    private Rate createRate(String iso, BigDecimal value) {
        Rate rate = new Rate();
        Currency currency = new Currency();
        currency.setIsoCode(iso);
        rate.setCurrency(currency);
        rate.setValue(value);

        return rate;
    }

}
