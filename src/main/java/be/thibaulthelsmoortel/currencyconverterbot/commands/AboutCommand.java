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
import be.thibaulthelsmoortel.currencyconverterbot.config.DiscordBotEnvironment;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
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

    private final DiscordBotEnvironment discordBotEnvironment;

    @Autowired
    public AboutCommand(DiscordBotEnvironment discordBotEnvironment) {
        this.discordBotEnvironment = discordBotEnvironment;
    }

    @Override
    public MessageEmbed call() {
        MessageEmbed embed = null;

        if (getEvent() instanceof SlashCommandInteractionEvent slashCommandInteractionEvent) {
            var embedBuilder = new EmbedBuilder();
            if (StringUtils.isAllBlank(discordBotEnvironment.getName(), discordBotEnvironment.getAuthor())) {
                embedBuilder.setTitle("Mystery bot by mystery author.");
            } else {
                StringBuilder descriptionBuilder = embedBuilder.getDescriptionBuilder();
                descriptionBuilder.append(String.format("%s%n%s%n", discordBotEnvironment.getName(), discordBotEnvironment.getDescription()));
                embedBuilder.addField("author", discordBotEnvironment.getAuthor(), true);
                embedBuilder.addField("version", discordBotEnvironment.getVersion(), true);
            }

            embed = embedBuilder.build();
            slashCommandInteractionEvent.getInteraction().replyEmbeds(embed).queue();
        }

        return embed;
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        return Commands.slash("about", "Provides general information about the bot.");
    }
}
