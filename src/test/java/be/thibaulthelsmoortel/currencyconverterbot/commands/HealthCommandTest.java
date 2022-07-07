/*
 * Copyright (c) 2022 Thibault Helsmoortel.
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import be.thibaulthelsmoortel.currencyconverterbot.client.health.payload.HealthResponse;
import be.thibaulthelsmoortel.currencyconverterbot.client.health.service.HealthServiceBean;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class HealthCommandTest extends CommandBaseTest {

    @InjectMocks
    private HealthCommand healthCommand;

    @Mock
    private HealthServiceBean healthServiceBean;

    @DisplayName("Should return health.")
    @Test
    void shouldReturnHealth() {
        healthCommand.setEvent(messageReceivedEvent);

        HealthResponse healthResponse = new HealthResponse();
        healthResponse.setStatus("UP");
        Mockito.when(healthServiceBean.getHealth()).thenReturn(healthResponse);

        String message = healthCommand.call();

        Assertions.assertNotNull(message, "Health must not be null.");
        Assertions.assertEquals("Status: " + healthServiceBean.getHealth().getStatus(), message, "Health must be correct.");

        verifyOneMessageSent();
    }

    @BeforeEach
    void setUp() {
        when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
        when(messageChannel.sendMessage(anyString())).thenReturn(mock(MessageAction.class));
    }

    private void verifyOneMessageSent() {
        verify(messageReceivedEvent).getChannel();
        verify(messageChannel).sendMessage(anyString());
        verifyNoMoreInteractions(messageChannel);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        verifyDoNotProcessEvent(healthCommand, mock(Event.class));
    }
}