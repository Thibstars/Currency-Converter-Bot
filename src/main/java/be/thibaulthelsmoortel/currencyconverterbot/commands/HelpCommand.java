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
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * @author Thibault Helsmoortel
 */
@Command(name = "help", description = "Provides command usage help.")
@Component
public class HelpCommand extends BotCommand {

    private final DiscordBotEnvironment discordBotEnvironment;
    private final List<BotCommand> botCommands;

    @Autowired
    public HelpCommand(DiscordBotEnvironment discordBotEnvironment,
        List<BotCommand> botCommands) {
        this.discordBotEnvironment = discordBotEnvironment;
        this.botCommands = botCommands;
    }

    @Override
    public Object call() {
        MessageBuilder messageBuilder = new MessageBuilder();
        if (getEvent() instanceof MessageReceivedEvent) {
            messageBuilder.setContent("Usage: " + discordBotEnvironment.getCommandPrefix() + "COMMAND [OPTIONS]")
                .appendFormat("%n%n%s%n%n", discordBotEnvironment.getDescription());
            createCommandOverview(messageBuilder);
            messageBuilder.appendFormat("%n%s '%s", "Run", discordBotEnvironment.getCommandPrefix())
                .append("COMMAND --help' for more information on a command.")
                .sendTo(((MessageReceivedEvent) getEvent()).getChannel())
                .queue();
        }

        String message = messageBuilder.getStringBuilder().toString();
        return StringUtils.isNotBlank(message) ? message : null;
    }

    private void createCommandOverview(MessageBuilder messageBuilder) {
        messageBuilder.appendFormat("%s %n", "Commands:");
        botCommands.forEach(botCommand -> {
            if (!(botCommand instanceof HelpCommand)) {
                Command annotation = botCommand.getClass().getAnnotation(Command.class);
                messageBuilder.appendFormat("%-15s %s %n", annotation.name(), parseDescription(annotation));
            }
        });
    }

    private String parseDescription(Command annotation) {
        String array = Arrays.toString(annotation.description());
        return array.substring(1, array.length() - 1);
    }
}
