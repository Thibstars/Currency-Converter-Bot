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

import be.thibaulthelsmoortel.currencyconverterbot.BaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.commands.core.CommandExecutor;
import be.thibaulthelsmoortel.currencyconverterbot.config.DiscordBotEnvironment;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

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
        Mockito.when(dblApi.setStats(ArgumentMatchers.anyInt())).thenReturn(new CompletableFuture<>());
    }

    @SuppressWarnings("unchecked")
    @DisplayName("Should handle message received.")
    @Test
    void shouldHandleMessageReceived() {
        Mockito.when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
        Mockito.when(messageChannel.sendTyping()).thenReturn(Mockito.mock(RestAction.class));
        ReceivedMessage receivedMessage = Mockito.mock(ReceivedMessage.class);
        Mockito.when(messageReceivedEvent.getMessage()).thenReturn(receivedMessage);
        Mockito.when(receivedMessage.getAuthor()).thenReturn(user);
        Mockito.when(user.isBot()).thenReturn(false);

        String prefix = "/";
        String message = "myNewMessage";
        Mockito.when(receivedMessage.getContentDisplay()).thenReturn(prefix + message);
        Mockito.when(discordBotEnvironment.getCommandPrefix()).thenReturn(prefix);

        discordBotRunner.onMessageReceived(messageReceivedEvent);

        Mockito.verify(messageChannel).sendTyping();
        Mockito.verifyNoMoreInteractions(messageChannel);
        Mockito.verify(commandExecutor).tryExecute(messageReceivedEvent, message);
        Mockito.verifyNoMoreInteractions(commandExecutor);
    }

    @DisplayName("Should not process bot messages.")
    @Test
    void shouldNotProcessBotMessages() {
        configureAsBot();
        Mockito.when(discordBotEnvironment.isProcessBotMessages()).thenReturn(false);

        discordBotRunner.onMessageReceived(messageReceivedEvent);

        Mockito.verifyNoMoreInteractions(messageChannel);
        Mockito.verify(messageReceivedEvent).getMessage(); // 1 to check processing
    }

    @SuppressWarnings("unchecked")
    @DisplayName("Should process bot messages.")
    @Test
    void shouldProcessBotMessages() {
        Message messageMock = configureAsBot();
        Mockito.when(discordBotEnvironment.isProcessBotMessages()).thenReturn(true);

        String prefix = "/";
        String message = "myNewMessage";
        Mockito.when(messageMock.getContentDisplay()).thenReturn(prefix + message);
        Mockito.when(discordBotEnvironment.getCommandPrefix()).thenReturn(prefix);
        Mockito.when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
        Mockito.when(messageChannel.sendTyping()).thenReturn(Mockito.mock(RestAction.class));

        discordBotRunner.onMessageReceived(messageReceivedEvent);

        Mockito.verify(messageChannel).sendTyping();
        Mockito.verifyNoMoreInteractions(messageChannel);
        Mockito.verify(commandExecutor).tryExecute(messageReceivedEvent, message);
        Mockito.verifyNoMoreInteractions(commandExecutor);
    }

    @DisplayName("Should update server count on guild join.")
    @Test
    void shouldUpdateServerCountOnGuildJoin() {
        GuildJoinEvent event = Mockito.mock(GuildJoinEvent.class);
        JDA jda = Mockito.mock(JDA.class);
        Mockito.when(event.getJDA()).thenReturn(jda);
        List<Guild> guilds = Collections.singletonList(Mockito.mock(Guild.class));
        Mockito.when(jda.getGuilds()).thenReturn(guilds);

        discordBotRunner.onGuildJoin(event);

        Mockito.verify(dblApi).setStats(guilds.size());
    }

    @DisplayName("Should update server count on guild leave.")
    @Test
    void shouldUpdateServerCountOnGuildLeave() {
        GuildLeaveEvent event = Mockito.mock(GuildLeaveEvent.class);
        JDA jda = Mockito.mock(JDA.class);
        Mockito.when(event.getJDA()).thenReturn(jda);
        List<Guild> guilds = Collections.singletonList(Mockito.mock(Guild.class));
        Mockito.when(jda.getGuilds()).thenReturn(guilds);

        discordBotRunner.onGuildLeave(event);

        Mockito.verify(dblApi).setStats(guilds.size());
    }

    private Message configureAsBot() {
        Message messageMock = Mockito.mock(Message.class);
        Mockito.when(messageReceivedEvent.getMessage()).thenReturn(messageMock);
        Mockito.when(messageMock.getAuthor()).thenReturn(user);
        Mockito.when(user.isBot()).thenReturn(true);

        return messageMock;
    }
}
