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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Thibault Helsmoortel
 */
class ConvertCommandTest extends CommandBaseTest {

    private ConvertCommand convertCommand;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        this.convertCommand = new ConvertCommand();
        convertCommand.setEvent(messageReceivedEvent);
    }

    @DisplayName("Should send convert message.")
    @Test
    void shouldSendConvertMessage() {
        String usdIso = "USD";
        String eurIso = "EUR";

        convertCommand.setSourceAmount(1);
        convertCommand.setSourceIsoCode(usdIso);
        convertCommand.setTargetIsoCode(eurIso);

        String message = convertCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertTrue(message.contains(eurIso), "Message should contain EUR.");
        Assertions.assertTrue(message.contains(usdIso), "Message should contain USD.");
        verifyOneMessageSent(message);
    }

    @DisplayName("Should send input not recognized message.")
    @Test
    void shouldSendInputNotRecognizedMessage() {
        String usdIso = "USD";

        String unrecognizedIsoCode = "Karman";

        convertCommand.setSourceAmount(6);
        convertCommand.setSourceIsoCode(usdIso);
        convertCommand.setTargetIsoCode(unrecognizedIsoCode);

        String message = convertCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(message), "Message should not be empty.");
        Assertions.assertEquals("Input parameters not recognized.", message, "Message should match.");
        verifyOneMessageSent(message);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        verifyDoNotProcessEvent(convertCommand, mock(Event.class));
    }

}
