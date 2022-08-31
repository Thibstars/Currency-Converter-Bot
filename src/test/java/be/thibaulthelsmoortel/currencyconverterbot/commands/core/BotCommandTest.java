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

package be.thibaulthelsmoortel.currencyconverterbot.commands.core;

import be.thibaulthelsmoortel.currencyconverterbot.client.conversion.service.ConversionService;
import be.thibaulthelsmoortel.currencyconverterbot.commands.CommandBaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.commands.ConvertCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import picocli.CommandLine;
import picocli.CommandLine.ParameterException;

/**
 * @author Thibault Helsmoortel
 */
class BotCommandTest extends CommandBaseTest {

    @InjectMocks
    private ConvertCommand convertCommand;

    @Mock
    private ConversionService conversionService;

    @Mock
    private CommandLine.Model.CommandSpec spec;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        convertCommand.setEvent(slashCommandInteractionEvent);
    }

    @DisplayName("Should throw ParameterException.")
    @Test
    void shouldThrowParameterException() {
        // Without setting parameters on the command, validations must fail

        Mockito.when(spec.commandLine()).thenReturn(Mockito.mock(CommandLine.class));

        ParameterException exception = Assertions.assertThrows(ParameterException.class, () -> convertCommand.call());
        Assertions.assertNotNull(exception, "Exception must not be null.");
        Assertions.assertTrue(exception.getMessage().contains("Error: "), "Message should contain at least one error.");

        Mockito.verifyNoInteractions(conversionService);
        verifyNoMoreJDAInteractions();
    }
}