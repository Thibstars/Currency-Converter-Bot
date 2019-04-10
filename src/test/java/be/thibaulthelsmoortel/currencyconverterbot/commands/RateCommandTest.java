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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * @author Thibault Helsmoortel
 */
class RateCommandTest extends CommandBaseTest {

    private RateCommand rateCommand;

    @MockBean
    private RatesParser ratesParser;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        this.rateCommand = new RateCommand(ratesParser);
        rateCommand.setEvent(messageReceivedEvent);
    }

    @DisplayName("Should send rate message.")
    @Test
    void shouldSendRateMessage() {
        String isoCode = "USD";
        Rate rate = createRate();
        when(ratesParser.parse(isoCode)).thenReturn(rate);
        rateCommand.setIsoCode(isoCode);
        String message = (String) rateCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertTrue(message.contains(isoCode), "Message should contain USD.");
        Assertions.assertTrue(message.contains("0.7532"), "Message should contain rate.");
        verifyOneMessageSent(message);
    }

    private Rate createRate() {
        Rate rate = new Rate();
        Currency currency = new Currency();
        currency.setIsoCode("USD");
        rate.setCurrency(currency);
        rate.setValue(new BigDecimal("0.7532"));

        return rate;
    }

    @DisplayName("Should send ISO code not found message.")
    @Test
    void shouldSendIsoCodeNotFoundMessage() {
        String isoCode = "myIsoCode";
        when(ratesParser.parse(isoCode)).thenThrow(NoSuchElementException.class);
        rateCommand.setIsoCode(isoCode);
        String message = (String) rateCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertEquals("Currency ISO code not found.", message, "Message should match.");
        verifyOneMessageSent(message);
    }

    @DisplayName("Should send could not find message when rate is null.")
    @Test
    void shouldSendCouldNotFindMessageWhenRateIsNull() {
        String isoCode = "USD";
        when(ratesParser.parse(isoCode)).thenReturn(null); // Simulates fail fetch
        rateCommand.setIsoCode(isoCode);
        String message = (String) rateCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertEquals("Couldn't find rate for specified ISO code.", message, "Message should match.");
        verifyOneMessageSent(message);
    }

    @AfterEach
    void tearDown() {
        rateCommand.setIsoCode(null);
    }
}
