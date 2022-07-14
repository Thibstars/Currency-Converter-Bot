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

import be.thibaulthelsmoortel.currencyconverterbot.BaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
public abstract class CommandBaseTest extends BaseTest {

    @Mock
    protected MessageReceivedEvent messageReceivedEvent;

    @Mock
    protected Message message;

    @Mock
    protected MessageChannel messageChannel;

    @SuppressWarnings("unchecked")
    @BeforeEach
    protected void setUp() {
        Mockito.when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
        Mockito.when(messageReceivedEvent.getMessage()).thenReturn(message);
        Mockito.when(message.addReaction(ArgumentMatchers.anyString())).thenReturn(Mockito.mock(RestAction.class));
        Mockito.when(messageChannel.sendMessage(ArgumentMatchers.anyString())).thenReturn(Mockito.mock(MessageAction.class));
    }

    void verifyOneMessageSent(String message) {
        Mockito.verify(messageReceivedEvent).getChannel();
        Mockito.verify(messageChannel).sendMessage(message);
        Mockito.verifyNoMoreInteractions(messageChannel);
        Mockito.verifyNoMoreInteractions(messageReceivedEvent);
    }

    void verifyOneMessageSent(MessageEmbed embed) {
        Mockito.verify(messageReceivedEvent).getChannel();
        Mockito.verify(messageChannel).sendMessageEmbeds(embed);
        Mockito.verifyNoMoreInteractions(messageChannel);
        Mockito.verifyNoMoreInteractions(messageReceivedEvent);
    }

    void verifyDoNotProcessEvent(BotCommand<?> botCommand, Event event) throws Exception {
        botCommand.setEvent(event);

        String message = (String) botCommand.call();

        Assertions.assertNull(message, "Message should not be processed.");
        verifyNoMoreJDAInteractions();
    }

    protected void verifyNoMoreJDAInteractions() {
        Mockito.verifyNoMoreInteractions(messageChannel);
        Mockito.verifyNoMoreInteractions(messageReceivedEvent);
    }

}
