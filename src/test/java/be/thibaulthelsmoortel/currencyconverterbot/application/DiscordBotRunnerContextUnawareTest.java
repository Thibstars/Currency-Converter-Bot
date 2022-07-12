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

package be.thibaulthelsmoortel.currencyconverterbot.application;

import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;
import static org.junit.jupiter.params.ParameterizedTest.INDEX_PLACEHOLDER;

import be.thibaulthelsmoortel.currencyconverterbot.commands.core.CommandExecutor;
import be.thibaulthelsmoortel.currencyconverterbot.config.DiscordBotEnvironment;
import be.thibaulthelsmoortel.currencyconverterbot.exceptions.MissingTokenException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class DiscordBotRunnerContextUnawareTest {

    private DiscordBotRunner discordBotRunner;

    private DiscordBotEnvironment discordBotEnvironment;

    @BeforeEach
    void setUp() {
        this.discordBotEnvironment = Mockito.mock(DiscordBotEnvironment.class);
        this.discordBotRunner = new DiscordBotRunner(discordBotEnvironment, Mockito.mock(CommandExecutor.class));
    }

    @DisplayName("Should throw MissingTokenException.")
    @ParameterizedTest(name = INDEX_PLACEHOLDER + ": " + ARGUMENTS_PLACEHOLDER)
    @MethodSource("blankStrings")
    void shouldThrowMissingTokenException(String token) {
        Mockito.when(discordBotEnvironment.getToken()).thenReturn(token);

        Assertions.assertThrows(MissingTokenException.class, () -> discordBotRunner.run(), "Exception should be thrown when no token was provided.");
        Assertions.assertThrows(MissingTokenException.class, () -> discordBotRunner.run(token), "Exception should be thrown when no token was provided.");
    }

    @DisplayName("Should not throw MissingTokenException.")
    @Test
    void shouldNotThrowMissingTokenException() {
        Mockito.when(discordBotEnvironment.getToken()).thenReturn("testToken");
        Assertions.assertDoesNotThrow(() -> discordBotRunner.run());
    }

    static Stream<String> blankStrings() {
        return Stream.of("", "   ", null);
    }

}
