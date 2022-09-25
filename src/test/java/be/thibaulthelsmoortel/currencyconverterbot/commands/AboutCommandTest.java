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

import be.thibaulthelsmoortel.currencyconverterbot.config.DiscordBotEnvironment;
import java.util.Objects;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class AboutCommandTest extends CommandBaseTest {

    @Mock
    private DiscordBotEnvironment discordBotEnvironment;

    @DisplayName("Should reply mystery.")
    @Test
    void shouldReplyMystery() {
        Mockito.when(discordBotEnvironment.getName()).thenReturn(null);
        Mockito.when(discordBotEnvironment.getAuthor()).thenReturn(null);
        AboutCommand command = new AboutCommand(discordBotEnvironment);
        command.setEvent(slashCommandInteractionEvent);

        Mockito.when(messageChannelUnion.sendMessageEmbeds(ArgumentMatchers.any(MessageEmbed.class)))
                .thenReturn(Mockito.mock(MessageCreateAction.class));

        MessageEmbed embed = command.call();

        Assertions.assertNotNull(embed, "Message should not be null.");
        Assertions.assertEquals("Mystery bot by mystery author.", embed.getTitle(), "Message should be correct.");

        verifyOneMessageReplied(embed);
    }

    @DisplayName("Should reply about message.")
    @Test
    void shouldReplyAboutMessage() {
        String name = "myBot";
        Mockito.when(discordBotEnvironment.getName()).thenReturn(name);
        String author = "myAuthor";
        Mockito.when(discordBotEnvironment.getAuthor()).thenReturn(author);
        String description = "my bot is the best";
        Mockito.when(discordBotEnvironment.getDescription()).thenReturn(description);
        String version = "1.1";
        Mockito.when(discordBotEnvironment.getVersion()).thenReturn(version);

        AboutCommand command = new AboutCommand(discordBotEnvironment);
        command.setEvent(slashCommandInteractionEvent);

        Mockito.when(messageChannelUnion.sendMessageEmbeds(ArgumentMatchers.any(MessageEmbed.class)))
                .thenReturn(Mockito.mock(MessageCreateAction.class));

        MessageEmbed embed = command.call();

        Assertions.assertNotNull(embed, "Message should not be null.");
        Assertions.assertNotNull(embed.getDescription(), "Description must not be null.");
        Assertions.assertTrue(embed.getDescription().contains(description), "Message should contain description.");
        Assertions.assertTrue(embed.getFields().stream()
                        .anyMatch(field -> Objects.equals(field.getValue(), author)),
                "Message should contain author.");
        Assertions.assertTrue(embed.getFields().stream()
                        .anyMatch(field -> Objects.equals(field.getValue(), version)),
                "Message should contain version.");
        verifyOneMessageReplied(embed);
    }

    @DisplayName("Should reply about message without bot name.")
    @Test
    void shouldReplyAboutMessageWithoutBotName() {
        Mockito.when(discordBotEnvironment.getName()).thenReturn(null);
        String author = "myAuthor";
        Mockito.when(discordBotEnvironment.getAuthor()).thenReturn(author);
        String description = "my bot is the best";
        Mockito.when(discordBotEnvironment.getDescription()).thenReturn(description);
        String version = "1.1";
        Mockito.when(discordBotEnvironment.getVersion()).thenReturn(version);

        AboutCommand command = new AboutCommand(discordBotEnvironment);
        command.setEvent(slashCommandInteractionEvent);

        Mockito.when(messageChannelUnion.sendMessageEmbeds(ArgumentMatchers.any(MessageEmbed.class)))
                .thenReturn(Mockito.mock(MessageCreateAction.class));

        MessageEmbed embed = command.call();

        Assertions.assertNotNull(embed, "Message should not be null.");
        Assertions.assertNotNull(embed.getDescription(), "Description must not be null.");
        Assertions.assertTrue(embed.getDescription().contains(description), "Message should contain description.");
        Assertions.assertTrue(embed.getFields().stream()
                        .anyMatch(field -> Objects.equals(field.getValue(), author)),
                "Message should contain author.");
        Assertions.assertTrue(embed.getFields().stream()
                        .anyMatch(field -> Objects.equals(field.getValue(), version)),
                "Message should contain version.");
        verifyOneMessageReplied(embed);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        AboutCommand command = new AboutCommand(discordBotEnvironment);

        verifyDoNotProcessEvent(command, Mockito.mock(Event.class));
    }

}
