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

import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * Command providing a donation url.
 *
 * @author Thibault Helsmoortel
 */
@Command(name = "donate", description = "Provides a donation url.")
@Component
public class DonateCommand extends BotCommand<String> {

    @Value("${bot.donation.url}")
    private String donationUrl;

    @Override
    public String call() {
        String message = null;
        if (getEvent() instanceof SlashCommandInteractionEvent slashCommandInteractionEvent) {
            message = donationUrl;

            slashCommandInteractionEvent.getInteraction().reply(message).queue();
        }

        return message;
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        return Commands.slash("donate", "Provides a donation url.");
    }
}
