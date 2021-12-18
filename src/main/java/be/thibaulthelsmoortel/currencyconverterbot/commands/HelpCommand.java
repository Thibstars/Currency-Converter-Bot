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
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * @author Thibault Helsmoortel
 */
@Command(name = "help", description = "Provides command usage help.")
@Component
public class HelpCommand extends BotCommand<MessageEmbed> {

    private final DiscordBotEnvironment discordBotEnvironment;
    private final List<BotCommand<?>> botCommands;

    @Autowired
    public HelpCommand(DiscordBotEnvironment discordBotEnvironment,
        List<BotCommand<?>> botCommands) {
        this.discordBotEnvironment = discordBotEnvironment;
        this.botCommands = botCommands;
    }

    @Override
    public MessageEmbed call() {
        var embedBuilder = new EmbedBuilder();
        if (getEvent() instanceof MessageReceivedEvent messageReceivedEvent) {
            StringBuilder descriptionBuilder = embedBuilder.getDescriptionBuilder();
            descriptionBuilder.append("Usage: ").append(discordBotEnvironment.getCommandPrefix()).append("COMMAND [OPTIONS]")
                .append(String.format("%n%n%s%n%n", discordBotEnvironment.getDescription()))
                .append(String.format("%s%n", "Commands:"));

            botCommands.forEach(botCommand -> {
                if (!(botCommand instanceof HelpCommand)) {
                    Command annotation = botCommand.getClass().getAnnotation(Command.class);
                    embedBuilder.addField(annotation.name(), parseDescription(annotation), false);
                }
            });

            MessageEmbed embed = embedBuilder.build();
            messageReceivedEvent.getChannel().sendMessage(embed).queue();

            return embed;
        }

        return null;
    }

    private String parseDescription(Command annotation) {
        var array = Arrays.toString(annotation.description());
        return array.substring(1, array.length() - 1);
    }
}
