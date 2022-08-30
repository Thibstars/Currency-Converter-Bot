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

package be.thibaulthelsmoortel.currencyconverterbot.application;

import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Thibault Helsmoortel
 */
@SpringBootTest
class CommandRegisterTest {

    @Autowired
    private CommandRegister commandRegister;

    @Autowired
    private List<BotCommand<?>> commands;

    @Mock
    private JDA jda;

    @DisplayName("Should register commands.")
    @Test
    void shouldRegisterCommands() {
        CommandListUpdateAction commandListUpdateAction = Mockito.mock(CommandListUpdateAction.class);
        Mockito.when(jda.updateCommands()).thenReturn(commandListUpdateAction);

        commandRegister.registerCommands(jda);

        Mockito.verify(commandListUpdateAction, Mockito.times(commands.size())).addCommands(ArgumentMatchers.any(SlashCommandData.class));
        Mockito.verify(commandListUpdateAction).queue();
    }
}