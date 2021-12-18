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

import static org.mockito.Mockito.mock;

import net.dv8tion.jda.api.events.Event;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Thibault Helsmoortel
 */
class RateCommandTest extends CommandBaseTest {

    private RateCommand rateCommand;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        this.rateCommand = new RateCommand();
        rateCommand.setEvent(messageReceivedEvent);
        rateCommand.setBaseCurrencyIsoCode("EUR");
    }

    @DisplayName("Should send rate message.")
    @Test
    void shouldSendRateMessage() {
        String isoCode = "USD";
        rateCommand.setIsoCode(isoCode);
        String message = rateCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertTrue(message.contains(isoCode), "Message should contain USD.");
        verifyOneMessageSent(message);
    }

    @DisplayName("Should send appropriate message with unavailable ISO code.")
    @Test
    void shouldSendAppropriateMessageWithUnavailableIsoCode() {
        String isoCode = "myIsoCode";
        rateCommand.setIsoCode(isoCode);
        String message = rateCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertEquals("Currency ISO code not found.", message, "Message should match.");
        verifyOneMessageSent(message);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        verifyDoNotProcessEvent(rateCommand, mock(Event.class));
    }

    @AfterEach
    void tearDown() {
        rateCommand.setIsoCode(null);
    }
}
