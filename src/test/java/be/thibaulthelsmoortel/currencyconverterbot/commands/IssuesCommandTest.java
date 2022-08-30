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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Thibault Helsmoortel
 */
class IssuesCommandTest extends CommandBaseTest  {

    private final IssuesCommand issuesCommand;

    @Value("${bot.issues.url}")
    private String issuesUrl;

    @Mock
    private JDA jda;

    @Autowired
    IssuesCommandTest(IssuesCommand issuesCommand) {
        this.issuesCommand = issuesCommand;
    }

    @DisplayName("Should return issues url.")
    @Test
    void shouldReturnInviteUrlWithoutPermissions() {
        issuesCommand.setEvent(slashCommandInteractionEvent);

        String message = issuesCommand.call();

        Assertions.assertNotNull(message, "Issues url must not be null.");
        Assertions.assertEquals(issuesUrl, message, "Issues url must be correct.");

        verifyOneMessageReplied();
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        verifyDoNotProcessEvent(issuesCommand, Mockito.mock(Event.class));
    }
}