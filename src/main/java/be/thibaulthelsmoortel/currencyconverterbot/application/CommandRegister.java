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
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.stereotype.Component;

/**
 * @author Thibault Helsmoortel
 */
@Component
@RequiredArgsConstructor
public class CommandRegister {

    private final List<BotCommand<?>> commands;

    public void registerCommands(JDA jda) {
        // These commandListUpdateAction might take a few minutes to be active after creation/update/delete
        CommandListUpdateAction commandListUpdateAction = jda.updateCommands();

        commands.forEach(cmd -> commandListUpdateAction.addCommands(cmd.getSlashCommandData()));

        // Send the new set of commandListUpdateAction to discord, this will override any existing global commandListUpdateAction with the new set provided here
        commandListUpdateAction.queue();
    }

}
