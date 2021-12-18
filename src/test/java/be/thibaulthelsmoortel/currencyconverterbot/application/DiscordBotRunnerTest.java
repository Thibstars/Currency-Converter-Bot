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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import be.thibaulthelsmoortel.currencyconverterbot.BaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.commands.core.CommandExecutor;
import be.thibaulthelsmoortel.currencyconverterbot.config.DiscordBotEnvironment;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.entities.ReceivedMessage;
import org.discordbots.api.client.DiscordBotListAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * @author Thibault Helsmoortel
 */
class DiscordBotRunnerTest extends BaseTest {

    private DiscordBotRunner discordBotRunner;

    @Mock
    private DiscordBotEnvironment discordBotEnvironment;

    @Mock
    private CommandExecutor commandExecutor;

    @Mock
    private MessageReceivedEvent messageReceivedEvent;

    @Mock
    private MessageChannel messageChannel;

    @Mock
    private User user;

    @Mock
    private DiscordBotListAPI dblApi;

    @BeforeEach
    void setUp() {
        this.discordBotRunner = new DiscordBotRunner(discordBotEnvironment, commandExecutor);
        discordBotRunner.setDblApi(dblApi);
    }

    @SuppressWarnings("unchecked")
    @DisplayName("Should handle message received.")
    @Test
    void shouldHandleMessageReceived() {
        when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
        when(messageChannel.sendTyping()).thenReturn(mock(RestAction.class));
        ReceivedMessage receivedMessage = mock(ReceivedMessage.class);
        when(messageReceivedEvent.getMessage()).thenReturn(receivedMessage);
        when(receivedMessage.getAuthor()).thenReturn(user);
        when(user.isBot()).thenReturn(false);

        String prefix = "/";
        String message = "myNewMessage";
        when(receivedMessage.getContentDisplay()).thenReturn(prefix + message);
        when(discordBotEnvironment.getCommandPrefix()).thenReturn(prefix);

        discordBotRunner.onMessageReceived(messageReceivedEvent);

        verify(messageChannel).sendTyping();
        verifyNoMoreInteractions(messageChannel);
        verify(commandExecutor).tryExecute(messageReceivedEvent, message);
        verifyNoMoreInteractions(commandExecutor);
    }

    @DisplayName("Should not process bot messages.")
    @Test
    void shouldNotProcessBotMessages() {
        configureAsBot();
        when(discordBotEnvironment.isProcessBotMessages()).thenReturn(false);

        discordBotRunner.onMessageReceived(messageReceivedEvent);

        verifyNoMoreInteractions(messageChannel);
        verify(messageReceivedEvent).getMessage(); // 1 to check processing
    }

    @SuppressWarnings("unchecked")
    @DisplayName("Should process bot messages.")
    @Test
    void shouldProcessBotMessages() {
        Message messageMock = configureAsBot();
        when(discordBotEnvironment.isProcessBotMessages()).thenReturn(true);

        String prefix = "/";
        String message = "myNewMessage";
        when(messageMock.getContentDisplay()).thenReturn(prefix + message);
        when(discordBotEnvironment.getCommandPrefix()).thenReturn(prefix);
        when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
        when(messageChannel.sendTyping()).thenReturn(mock(RestAction.class));

        discordBotRunner.onMessageReceived(messageReceivedEvent);

        verify(messageChannel).sendTyping();
        verifyNoMoreInteractions(messageChannel);
        verify(commandExecutor).tryExecute(messageReceivedEvent, message);
        verifyNoMoreInteractions(commandExecutor);
    }

    @DisplayName("Should update server count on guild join.")
    @Test
    void shouldUpdateServerCountOnGuildJoin() {
        GuildJoinEvent event = mock(GuildJoinEvent.class);
        JDA jda = mock(JDA.class);
        when(event.getJDA()).thenReturn(jda);
        List<Guild> guilds = Collections.singletonList(mock(Guild.class));
        when(jda.getGuilds()).thenReturn(guilds);

        discordBotRunner.onGuildJoin(event);

        verify(dblApi).setStats(guilds.size());
    }

    @DisplayName("Should update server count on guild leave.")
    @Test
    void shouldUpdateServerCountOnGuildLeave() {
        GuildLeaveEvent event = mock(GuildLeaveEvent.class);
        JDA jda = mock(JDA.class);
        when(event.getJDA()).thenReturn(jda);
        List<Guild> guilds = Collections.singletonList(mock(Guild.class));
        when(jda.getGuilds()).thenReturn(guilds);

        discordBotRunner.onGuildLeave(event);

        verify(dblApi).setStats(guilds.size());
    }

    private Message configureAsBot() {
        Message messageMock = mock(Message.class);
        when(messageReceivedEvent.getMessage()).thenReturn(messageMock);
        when(messageMock.getAuthor()).thenReturn(user);
        when(user.isBot()).thenReturn(true);

        return messageMock;
    }
}
