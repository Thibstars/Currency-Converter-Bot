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

import static org.mockito.Mockito.mock;

import net.dv8tion.jda.core.events.Event;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Thibault Helsmoortel
 */
class RatesCommandTest extends CommandBaseTest {

    private RatesCommand ratesCommand;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        this.ratesCommand = new RatesCommand();
        ratesCommand.setEvent(messageReceivedEvent);
    }

    @DisplayName("Should send rates message.")
    @Test
    void shouldSendRatesMessage() throws Exception {
        String message = (String) ratesCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertTrue(message.contains("USD"), "Message should contain USD.");
        verifyOneMessageSent(message);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        RatesCommand ratesCommand = new RatesCommand();

        verifyDoNotProcessEvent(ratesCommand, mock(Event.class));
    }
}
