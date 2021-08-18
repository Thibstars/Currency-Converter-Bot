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

package be.thibaulthelsmoortel.currencyconverterbot.commands.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import be.thibaulthelsmoortel.currencyconverterbot.BaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.commands.AboutCommand;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine.Command;

/**
 * @author Thibault Helsmoortel
 */
class CommandExecutorTest extends BaseTest {

    private final CommandExecutor commandExecutor;

    private final AboutCommand aboutCommand;

    @Mock
    private MessageReceivedEvent messageReceivedEvent;

    @Mock
    private MessageChannel messageChannel;

    @Autowired
    CommandExecutorTest(CommandExecutor commandExecutor, AboutCommand aboutCommand) {
        this.commandExecutor = commandExecutor;
        this.aboutCommand = aboutCommand;
    }

    @BeforeEach
    void setUp() {
        when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
        MessageAction messageAction = mock(MessageAction.class);
        when(messageChannel.sendMessage(anyString())).thenReturn(messageAction);
        when(messageChannel.sendMessage(any(MessageEmbed.class))).thenReturn(messageAction);
    }

    @DisplayName("Should execute command.")
    @Test
    void shouldExecuteCommand() {
        String commandName = aboutCommand.getClass().getAnnotation(Command.class).name();

        aboutCommand.setEvent(messageReceivedEvent);
        boolean executed = commandExecutor.tryExecute(messageReceivedEvent, commandName);

        // Assuming the command sends a message back:
        verify(messageReceivedEvent, times(2)).getChannel(); // Once for sending the message, once to pass to the output stream
        verify(messageChannel).sendMessage(any(MessageEmbed.class));
        verifyNoMoreInteractions(messageChannel);
        verifyNoMoreInteractions(messageReceivedEvent);

        Assertions.assertTrue(executed, "Command should be executed.");
    }

    @DisplayName("Should execute command with arguments.")
    @Test
    void shouldExecuteCommandWithArguments() {
        String commandName = aboutCommand.getClass().getAnnotation(Command.class).name() + " -h";

        aboutCommand.setEvent(messageReceivedEvent);
        boolean executed = commandExecutor.tryExecute(messageReceivedEvent, commandName);

        // Assuming the command sends a message back:
        verify(messageReceivedEvent).getChannel(); // Once for sending the message, once to pass to the output stream
        verify(messageChannel).sendMessage(any(MessageEmbed.class));
        verifyNoMoreInteractions(messageChannel);
        verifyNoMoreInteractions(messageReceivedEvent);

        Assertions.assertTrue(executed, "Command should be executed.");
    }

    @DisplayName("Should not execute command.")
    @Test
    void shouldNotExecuteCommand() {
        String commandName = "someUnavailableCommand";

        boolean executed = commandExecutor.tryExecute(messageReceivedEvent, commandName);

        // The executor should send back a message:
        verify(messageReceivedEvent).getChannel();
        verify(messageChannel).sendMessage("Command not recognized...");
        verifyNoMoreInteractions(messageChannel);
        verifyNoMoreInteractions(messageReceivedEvent);

        Assertions.assertFalse(executed, "Command should not be executed.");
    }
}
