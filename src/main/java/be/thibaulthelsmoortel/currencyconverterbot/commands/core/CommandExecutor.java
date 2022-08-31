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

package be.thibaulthelsmoortel.currencyconverterbot.commands.core;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;
import picocli.CommandLine.Help.Ansi;

/**
 * Class responsible for command execution.
 *
 * @author Thibault Helsmoortel
 */
@Component
@Slf4j
public class CommandExecutor {

    private final List<BotCommand<?>> botCommands;
    private final MessageChannelOutputStream messageChannelOutputStream;
    private final PrintWriter printWriter;

    @Autowired
    public CommandExecutor(List<BotCommand<?>> botCommands, MessageChannelOutputStream messageChannelOutputStream) {
        this.botCommands = botCommands;
        this.messageChannelOutputStream = messageChannelOutputStream;
        printWriter = new PrintWriter(messageChannelOutputStream);
    }

    /**
     * Tries to execute a command.
     *
     * @param event the raised JDA event
     * @param commandMessage the command message (stripped from its prefix)
     * @return true if the command was executed, false if otherwise
     */
    public boolean tryExecute(SlashCommandInteractionEvent event, String commandMessage) {
        var commandRecognised = new AtomicBoolean(false);

        if (StringUtils.isNotBlank(commandMessage)) {
            botCommands.forEach(command -> {
                var commandType = command.getClass().getAnnotation(Command.class);
                String commandName = commandType.name();

                if (commandMessage.split(" ")[0].equals(commandName)) {
                    commandRecognised.set(true);
                    command.setEvent(event);
                    String args = commandMessage.substring(commandMessage.indexOf(commandType.name()) + commandType.name().length()).trim();

                    messageChannelOutputStream.setMessageChannel(event.getChannel());

                    var commandLine = new CommandLine(command);
                    commandLine.setColorScheme(Help.defaultColorScheme(Ansi.OFF));
                    commandLine.setOut(printWriter);
                    commandLine.setErr(printWriter);

                    if (StringUtils.isNotBlank(args)) {
                        List<String> targetArglist = stripOptionsFromArgs(command, args);
                        commandLine.execute(targetArglist.toArray(new String[0]));
                    } else {
                        commandLine.execute();
                    }
                }
            });

            if (commandRecognised.get()) {
                log.debug("Executed command: {}.", commandMessage);
            } else {
                log.debug("Command not recognized: {}.", commandMessage);
                event.reply("Command not recognized... Issue the 'help' command to get an overview of available commands.").queue();
            }
        }

        return commandRecognised.get();
    }

    @NotNull
    private static List<String> stripOptionsFromArgs(BotCommand<?> command, String args) {
        List<String> originalArgList = new ArrayList<>(Arrays.asList(args.split(" ")));
        List<String> targetArglist = new ArrayList<>();
        originalArgList.forEach(arg -> {
                    List<OptionData> options = command.getSlashCommandData().getOptions();
                    boolean argIsParamName = options.stream()
                            .map(OptionData::getName)
                            .anyMatch(arg::startsWith);
                    if (!argIsParamName) {
                        targetArglist.add(arg);
                    }
                });

        return targetArglist;
    }

}
