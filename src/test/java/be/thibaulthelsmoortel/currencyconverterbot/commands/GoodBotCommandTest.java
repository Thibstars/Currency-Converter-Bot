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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import net.dv8tion.jda.core.events.Event;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Thibault Helsmoortel
 */
class GoodBotCommandTest extends CommandBaseTest {

    private final GoodBotCommand goodBotCommand;

    @Autowired
    GoodBotCommandTest(GoodBotCommand goodBotCommand) {
        this.goodBotCommand = goodBotCommand;
    }

    @DisplayName("Should add reaction to message.")
    @Test
    void shouldAddReactionToMessage() {
        goodBotCommand.setEvent(messageReceivedEvent);
        String emoji = (String) goodBotCommand.call();

        Assertions.assertTrue(StringUtils.isNotBlank(emoji), "Emoji must not be blank.");

        verify(messageReceivedEvent).getMessage();
        verify(message).addReaction(emoji);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        verifyDoNotProcessEvent(goodBotCommand, mock(Event.class));
    }
}
