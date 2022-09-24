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

import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import be.thibaulthelsmoortel.currencyconverterbot.config.DiscordBotEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import picocli.CommandLine.Command;

/**
 * @author Thibault Helsmoortel
 */
class HelpCommandTest extends CommandBaseTest {

    private List<BotCommand<?>> botCommands;

    @DisplayName("Should send help message.")
    @Test
    void shouldSendHelpMessage() {
        botCommands = new ArrayList<>();
        botCommands.add(new InviteCommand());
        botCommands.add(Mockito.mock(HelpCommand.class));

        DiscordBotEnvironment environment = Mockito.mock(DiscordBotEnvironment.class);
        Mockito.when(environment.getCommandPrefix()).thenReturn("/");
        HelpCommand command = new HelpCommand(environment, botCommands);
        command.setEvent(slashCommandInteractionEvent);

        Mockito.when(messageChannelUnion.getType()).thenReturn(ChannelType.UNKNOWN);
        Mockito.when(messageChannelUnion.getId()).thenReturn("id");
        ReplyCallbackAction replyCallbackAction = Mockito.mock(ReplyCallbackAction.class);
        Mockito.when(slashCommandInteraction.replyEmbeds(ArgumentMatchers.any(MessageEmbed.class)))
                .thenReturn(replyCallbackAction);

        MessageEmbed embedded = command.call();
        Assertions.assertNotNull(embedded, "Message must not be null.");
        Assertions.assertTrue(StringUtils.isNotBlank(embedded.getDescription()), "Description should not be empty.");
        Assertions.assertNotNull(embedded.getFields(), "Message fields must not be null.");
        Assertions.assertFalse(embedded.getFields().isEmpty(), "Message fields must not be empty.");
        botCommands.forEach(botCommand -> {
            if (!(botCommand instanceof HelpCommand)) {
                Command annotation = botCommand.getClass().getAnnotation(Command.class);
                Assertions.assertTrue(embedded.getFields().stream()
                                .anyMatch(field -> Objects.equals(field.getName(), annotation.name())),
                        "Message should contain command name.");
                Assertions.assertTrue(embedded.getFields().stream()
                                .anyMatch(field -> Objects.equals(field.getValue(), parseDescription(annotation))),
                        "Message should contain command description.");
            }
        });

        verifyOneMessageSent(replyCallbackAction);
    }

    void verifyOneMessageSent(ReplyCallbackAction replyCallbackAction) {
        Mockito.verify(replyCallbackAction).queue();
    }

    private String parseDescription(Command annotation) {
        String array = Arrays.toString(annotation.description());
        return array.substring(1, array.length() - 1);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        HelpCommand command = new HelpCommand(Mockito.mock(DiscordBotEnvironment.class), botCommands);

        verifyDoNotProcessEvent(command, Mockito.mock(Event.class));
    }

}
