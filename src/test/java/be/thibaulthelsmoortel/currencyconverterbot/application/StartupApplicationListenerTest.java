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
import static org.mockito.Mockito.when;

import be.thibaulthelsmoortel.currencyconverterbot.BaseTest;
import be.thibaulthelsmoortel.currencyconverterbot.config.DiscordBotEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.context.event.ApplicationStartedEvent;

/**
 * @author Thibault Helsmoortel
 */
class StartupApplicationListenerTest extends BaseTest {

    private StartupApplicationListener listener;

    @Mock
    private DiscordBotEnvironment discordBotEnvironment;

    @Mock
    private Warmup warmup;

    @BeforeEach
    void setUp() {
        this.listener = new StartupApplicationListener(discordBotEnvironment, warmup);

        when(discordBotEnvironment.getAuthor()).thenReturn("author");
        when(discordBotEnvironment.getName()).thenReturn("bot");
        when(discordBotEnvironment.getVersion()).thenReturn("test");
    }

    @DisplayName("Should perform startup tasks.")
    @Test
    void shouldPerformStartupTasks() {
        listener.onApplicationEvent(mock(ApplicationStartedEvent.class));

        verify(warmup).perform();
    }
}