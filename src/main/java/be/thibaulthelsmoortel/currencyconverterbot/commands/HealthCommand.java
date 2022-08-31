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

import be.thibaulthelsmoortel.currencyconverterbot.client.health.service.HealthServiceBean;
import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * Basic command checking the API health.
 *
 * @author Thibault Helsmoortel
 */
@RequiredArgsConstructor
@Command(name = "health", description = "Checks the API health.")
@Component
public class HealthCommand extends BotCommand<String> {

    private final HealthServiceBean healthServiceBean;

    @Override
    public String call() {
        String message = null;
        if (getEvent() instanceof SlashCommandInteractionEvent slashCommandInteractionEvent) {
            message = "Status: " + healthServiceBean.getHealth().getStatus();

            slashCommandInteractionEvent.getInteraction().reply(message).queue();
        }

        return message;
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        return Commands.slash("health", "Checks the API health.");
    }
}
