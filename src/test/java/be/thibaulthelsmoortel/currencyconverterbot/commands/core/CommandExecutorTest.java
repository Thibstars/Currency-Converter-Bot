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

package be.thibaulthelsmoortel.currencyconverterbot.commands.core;

import be.thibaulthelsmoortel.currencyconverterbot.BaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.commands.AboutCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine.Command;

/**
 * @author Thibault Helsmoortel
 */
class CommandExecutorTest extends BaseTest {

    private final CommandExecutor commandExecutor;

    private final AboutCommand aboutCommand;

    @Mock
    private SlashCommandInteractionEvent slashCommandInteractionEvent;

    @Mock
    private SlashCommandInteraction slashCommandInteraction;

    @Mock
    private MessageChannelUnion messageChannelUnion;

    @Autowired
    CommandExecutorTest(CommandExecutor commandExecutor, AboutCommand aboutCommand) {
        this.commandExecutor = commandExecutor;
        this.aboutCommand = aboutCommand;
    }

    @BeforeEach
    void setUp() {
        Mockito.when(slashCommandInteractionEvent.getChannel()).thenReturn(messageChannelUnion);
        Mockito.when(slashCommandInteractionEvent.getInteraction()).thenReturn(slashCommandInteraction);
        Mockito.when(slashCommandInteraction.replyEmbeds(ArgumentMatchers.any(MessageEmbed.class)))
                .thenReturn(Mockito.mock(ReplyCallbackAction.class));

        MessageCreateAction messageCreateAction = Mockito.mock(MessageCreateAction.class);
        Mockito.when(messageChannelUnion.sendMessage(ArgumentMatchers.anyString())).thenReturn(messageCreateAction);
        Mockito.when(messageChannelUnion.sendMessageEmbeds(ArgumentMatchers.any(MessageEmbed.class))).thenReturn(messageCreateAction);
    }

    @DisplayName("Should execute command.")
    @Test
    void shouldExecuteCommand() {
        String commandName = aboutCommand.getClass().getAnnotation(Command.class).name();

        aboutCommand.setEvent(slashCommandInteractionEvent);
        boolean executed = commandExecutor.tryExecute(slashCommandInteractionEvent, commandName);

        // Assuming the command sends a message back:
        Mockito.verify(slashCommandInteractionEvent).getChannel();
        Mockito.verify(slashCommandInteraction).replyEmbeds(ArgumentMatchers.any(MessageEmbed.class));
        Mockito.verifyNoMoreInteractions(messageChannelUnion);

        Assertions.assertTrue(executed, "Command should be executed.");
    }

    @DisplayName("Should execute command with arguments.")
    @Test
    void shouldExecuteCommandWithArguments() {
        String commandName = aboutCommand.getClass().getAnnotation(Command.class).name() + " -h";

        aboutCommand.setEvent(slashCommandInteractionEvent);
        boolean executed = commandExecutor.tryExecute(slashCommandInteractionEvent, commandName);

        // Assuming the command sends a message back:
        Mockito.verify(slashCommandInteractionEvent).getChannel(); // Once for sending the message, once to pass to the output stream
        Mockito.verify(messageChannelUnion).sendMessageEmbeds(Mockito.any(MessageEmbed.class));
        Mockito.verifyNoMoreInteractions(messageChannelUnion);
        Mockito.verifyNoMoreInteractions(slashCommandInteractionEvent);

        Assertions.assertTrue(executed, "Command should be executed.");
    }

    @DisplayName("Should not execute command.")
    @Test
    void shouldNotExecuteCommand() {
        String commandName = "someUnavailableCommand";
        String commandNotRecognizedMessage = "Command not recognized... Issue the 'help' command to get an overview of available commands.";

        ReplyCallbackAction replyCallbackAction = Mockito.mock(ReplyCallbackAction.class);
        Mockito.when(slashCommandInteractionEvent.reply(commandNotRecognizedMessage))
                .thenReturn(replyCallbackAction);

        boolean executed = commandExecutor.tryExecute(slashCommandInteractionEvent, commandName);

        // The executor should send back a message:
        Mockito.verify(slashCommandInteractionEvent).reply(commandNotRecognizedMessage);
        Mockito.verify(replyCallbackAction).queue();
        Mockito.verifyNoMoreInteractions(messageChannelUnion);
        Mockito.verifyNoMoreInteractions(slashCommandInteractionEvent);

        Assertions.assertFalse(executed, "Command should not be executed.");
    }
}
