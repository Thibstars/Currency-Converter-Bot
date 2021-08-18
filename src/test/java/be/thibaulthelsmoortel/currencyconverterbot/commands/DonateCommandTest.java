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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Thibault Helsmoortel
 */
class DonateCommandTest extends CommandBaseTest {

    private final DonateCommand donateCommand;

    @Value("${bot.donation.url}")
    private String donationUrl;

    @Mock
    private JDA jda;

    @Autowired
    DonateCommandTest(DonateCommand donateCommand) {
        this.donateCommand = donateCommand;
    }

    @BeforeEach
    void setUp() {
        when(messageReceivedEvent.getJDA()).thenReturn(jda);
        when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
        when(messageChannel.sendMessage(anyString())).thenReturn(mock(MessageAction.class));
    }

    @DisplayName("Should return donation url.")
    @Test
    void shouldReturnInviteUrlWithoutPermissions() {
        donateCommand.setEvent(messageReceivedEvent);

        String message = donateCommand.call();

        Assertions.assertNotNull(message, "Donation url must not be null.");
        Assertions.assertEquals(donationUrl, message, "Donation url must be correct.");

        verifyOneMessageSent();
    }

    private void verifyOneMessageSent() {
        verify(messageReceivedEvent).getChannel();
        verify(messageChannel).sendMessage(anyString());
        verifyNoMoreInteractions(messageChannel);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        verifyDoNotProcessEvent(donateCommand, mock(Event.class));
    }
}
