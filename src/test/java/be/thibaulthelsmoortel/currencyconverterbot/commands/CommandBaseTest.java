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
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
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
    protected SlashCommandInteractionEvent slashCommandInteractionEvent;

    @Mock
    protected SlashCommandInteraction slashCommandInteraction;

    @Mock
    protected MessageChannelUnion messageChannelUnion;

    @BeforeEach
    protected void setUp() {
        Mockito.when(slashCommandInteractionEvent.getChannel()).thenReturn(messageChannelUnion);
        Mockito.when(slashCommandInteractionEvent.getInteraction()).thenReturn(slashCommandInteraction);
        Mockito.when(slashCommandInteraction.reply(ArgumentMatchers.anyString()))
                .thenReturn(Mockito.mock(ReplyCallbackAction.class));
        Mockito.when(slashCommandInteraction.replyEmbeds(ArgumentMatchers.any(MessageEmbed.class)))
                .thenReturn(Mockito.mock(ReplyCallbackAction.class));
        Mockito.when(messageChannelUnion.sendMessage(ArgumentMatchers.anyString()))
                .thenReturn(Mockito.mock(MessageCreateAction.class));
    }

    void verifyOneMessageReplied() {
        Mockito.verify(slashCommandInteractionEvent).getInteraction();
        Mockito.verify(slashCommandInteraction).reply(ArgumentMatchers.anyString());
    }

    void verifyOneMessageReplied(MessageEmbed embed) {
        Mockito.verify(slashCommandInteractionEvent).getInteraction();
        Mockito.verify(slashCommandInteraction).replyEmbeds(embed);
    }

    void verifyOneMessageReplied(String message) {
        Mockito.verify(slashCommandInteractionEvent).getInteraction();
        Mockito.verify(slashCommandInteraction).reply(message);
    }

    void verifyDoNotProcessEvent(BotCommand<?> botCommand, Event event) throws Exception {
        botCommand.setEvent(event);

        String message = (String) botCommand.call();

        Assertions.assertNull(message, "Message should not be processed.");
        verifyNoMoreJDAInteractions();
    }

    protected void verifyNoMoreJDAInteractions() {
        Mockito.verifyNoMoreInteractions(messageChannelUnion);
        Mockito.verifyNoMoreInteractions(slashCommandInteractionEvent);
    }

}
