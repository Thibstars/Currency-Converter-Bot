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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Thibault Helsmoortel
 */
class InviteCommandTest extends CommandBaseTest {

    private static final String EXPECTED_SCOPE = "scope=bot";
    private static final String PERMISSIONS_PARAM = "permissions=";

    private static final String INVITE_URL_WITH_PERMISSIONS = "https://discordapp.com/oauth2/authorize?scope=bot&client_id=446990121802618890&permissions=3072";
    private static final String INVITE_URL_NO_PERMISSIONS = "https://discordapp.com/oauth2/authorize?scope=bot&client_id=446990121802618890";

    private final InviteCommand inviteCommand;

    @Mock
    private JDA jda;

    @Autowired
    InviteCommandTest(InviteCommand inviteCommand) {
        this.inviteCommand = inviteCommand;
    }

    @BeforeEach
    protected void setUp() {
        Mockito.when(messageReceivedEvent.getJDA()).thenReturn(jda);
        Mockito.when(jda.getInviteUrl(Permission.EMPTY_PERMISSIONS)).thenReturn(INVITE_URL_NO_PERMISSIONS);
        Mockito.when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
        Mockito.when(messageChannel.sendMessage(ArgumentMatchers.anyString())).thenReturn(Mockito.mock(MessageAction.class));
    }

    @DisplayName("Should return invite url without permissions.")
    @Test
    void shouldReturnInviteUrlWithoutPermissions() {
        inviteCommand.setEvent(messageReceivedEvent);

        String message = inviteCommand.call();

        Assertions.assertNotNull(message, "Invite url must not be null.");
        Assertions.assertTrue(message.contains(EXPECTED_SCOPE), "Scope must be correct.");
        Assertions.assertFalse(message.contains(PERMISSIONS_PARAM), "No permissions should be provided.");

        verifyOneMessageSent();
    }

    private void verifyOneMessageSent() {
        Mockito.verify(messageReceivedEvent).getChannel();
        Mockito.verify(messageChannel).sendMessage(ArgumentMatchers.anyString());
        Mockito.verifyNoMoreInteractions(messageChannel);
    }

    @DisplayName("Should return invite url with permissions.")
    @Test
    void shouldReturnInviteUrlWithPermissions() {
        Permission[] permissions = {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE};
        inviteCommand.setPermissionsRequested(new boolean[]{true});
        inviteCommand.setPermissions(permissions);

        Mockito.when(jda.getInviteUrl(permissions)).thenReturn(INVITE_URL_WITH_PERMISSIONS);
        inviteCommand.setEvent(messageReceivedEvent);

        String message = inviteCommand.call();
        Assertions.assertNotNull(message, "Invite url must not be null.");
        Assertions.assertTrue(message.contains(EXPECTED_SCOPE), "Scope must be correct.");
        Assertions.assertTrue(message.contains(PERMISSIONS_PARAM), "Permissions should be provided.");

        verifyOneMessageSent();
    }

    @DisplayName("Should return invite url without permissions when none available.")
    @Test
    void shouldReturnInviteUrlWithoutPermissionsWhenNoneAvailable() {
        inviteCommand.setPermissionsRequested(new boolean[]{true});
        inviteCommand.setPermissions(null);

        Mockito.when(jda.getInviteUrl(ArgumentMatchers.any(Permission[].class))).thenReturn(INVITE_URL_WITH_PERMISSIONS);
        inviteCommand.setEvent(messageReceivedEvent);

        String message = inviteCommand.call();
        Assertions.assertNotNull(message, "Invite url must not be null.");
        Assertions.assertTrue(message.contains(EXPECTED_SCOPE), "Scope must be correct.");
        Assertions.assertTrue(message.contains(PERMISSIONS_PARAM), "Permissions should be provided.");

        verifyOneMessageSent();
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        verifyDoNotProcessEvent(inviteCommand, Mockito.mock(Event.class));
    }
}
