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
import static org.mockito.Mockito.when;

import be.thibaulthelsmoortel.currencyconverterbot.config.DiscordBotEnvironment;
import net.dv8tion.jda.api.events.Event;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * @author Thibault Helsmoortel
 */
class AboutCommandTest extends CommandBaseTest {

    @Mock
    private DiscordBotEnvironment discordBotEnvironment;

    @DisplayName("Should reply mystery.")
    @Test
    void shouldReplyMystery() {
        when(discordBotEnvironment.getName()).thenReturn(null);
        when(discordBotEnvironment.getAuthor()).thenReturn(null);
        AboutCommand command = new AboutCommand(discordBotEnvironment);
        command.setEvent(messageReceivedEvent);
        String message = (String) command.call();

        Assertions.assertEquals("Mystery bot by mystery author.", message, "Message should be correct.");

        verifyOneMessageSent(message);
    }

    @DisplayName("Should reply about message.")
    @Test
    void shouldReplyAboutMessage() {
        String name = "myBot";
        when(discordBotEnvironment.getName()).thenReturn(name);
        String author = "myAuthor";
        when(discordBotEnvironment.getAuthor()).thenReturn(author);
        String description = "my bot is the best";
        when(discordBotEnvironment.getDescription()).thenReturn(description);

        AboutCommand command = new AboutCommand(discordBotEnvironment);
        command.setEvent(messageReceivedEvent);

        String message = (String) command.call();

        Assertions.assertEquals(name + " created by " + author + "." + System.lineSeparator() + description, message, "Message should be correct.");

        verifyOneMessageSent(message);
    }

    @DisplayName("Should reply about message without bot name.")
    @Test
    void shouldReplyAboutMessageWithoutBotName() {
        when(discordBotEnvironment.getName()).thenReturn(null);
        String author = "myAuthor";
        when(discordBotEnvironment.getAuthor()).thenReturn(author);
        String description = "my bot is the best";
        when(discordBotEnvironment.getDescription()).thenReturn(description);

        AboutCommand command = new AboutCommand(discordBotEnvironment);
        command.setEvent(messageReceivedEvent);

        String message = (String) command.call();

        Assertions.assertEquals("Bot created by " + author + "." + System.lineSeparator() + description, message, "Message should be correct.");

        verifyOneMessageSent(message);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        AboutCommand command = new AboutCommand(discordBotEnvironment);

        verifyDoNotProcessEvent(command, mock(Event.class));
    }

}
