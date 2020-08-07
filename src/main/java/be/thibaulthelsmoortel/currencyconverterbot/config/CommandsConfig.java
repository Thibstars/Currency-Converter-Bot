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

package be.thibaulthelsmoortel.currencyconverterbot.config;

import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import picocli.CommandLine;

/**
 * Configuration preparing commands.
 *
 * @author Thibault Helsmoortel
 */
@Configuration
public class CommandsConfig {

    private final ListableBeanFactory listableBeanFactory;

    @Autowired
    public CommandsConfig(ListableBeanFactory listableBeanFactory) {
        this.listableBeanFactory = listableBeanFactory;
    }

    @Bean
    public List<BotCommand> commands() {
        Map<String, Object> commands = listableBeanFactory.getBeansWithAnnotation(CommandLine.Command.class);

        return commands.values().stream()
            .filter(command -> command instanceof BotCommand)
            .map(command -> (BotCommand) command)
            .collect(Collectors.toList());
    }

}
