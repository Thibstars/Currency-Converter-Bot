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

import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import be.thibaulthelsmoortel.currencyconverterbot.config.DiscordBotEnvironment;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * Basic command providing general information on the bot.
 *
 * @author Thibault Helsmoortel
 */
@Command(name = "about", description = "Provides general information about the bot.")
@Component
public class AboutCommand extends BotCommand<MessageEmbed> {

    private static final String FALLBACK = "Bot";

    private final DiscordBotEnvironment discordBotEnvironment;

    @Autowired
    public AboutCommand(DiscordBotEnvironment discordBotEnvironment) {
        this.discordBotEnvironment = discordBotEnvironment;
    }

    @Override
    public MessageEmbed call() {
        MessageEmbed embed = null;

        if (getEvent() instanceof MessageReceivedEvent messageReceivedEvent) {
            var embedBuilder = new EmbedBuilder();
            if (StringUtils.isAllBlank(discordBotEnvironment.getName(), discordBotEnvironment.getAuthor())) {
                embedBuilder.setTitle("Mystery bot by mystery author.");
            } else {
                embedBuilder.setDescription(whenNotBlankPrint(discordBotEnvironment.getName())
                    + (StringUtils.isNotBlank(discordBotEnvironment.getAuthor()) ? " created by " + discordBotEnvironment.getAuthor() + "." : "")
                    + (StringUtils.isNotBlank(discordBotEnvironment.getVersion()) ? " Version: " + discordBotEnvironment.getVersion() : "")
                    + (StringUtils.isNotBlank(discordBotEnvironment.getDescription()) ? System.lineSeparator() + discordBotEnvironment.getDescription() : ""));
            }

            embed = embedBuilder.build();
            messageReceivedEvent.getChannel().sendMessage(embed).queue();
        }

        return embed;
    }

    private String whenNotBlankPrint(String toPrint) {
        if (StringUtils.isNotBlank(toPrint)) {
            return toPrint;
        } else {
            return FALLBACK;
        }
    }
}
