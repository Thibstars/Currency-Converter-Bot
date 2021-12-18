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

package be.thibaulthelsmoortel.currencyconverterbot.application;

import be.thibaulthelsmoortel.currencyconverterbot.config.DiscordBotEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Startup application listener performing tasks upon startup.
 *
 * @author Thibault Helsmoortel
 */
@Component
public class StartupApplicationListener implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupApplicationListener.class);

    private final DiscordBotEnvironment discordBotEnvironment;

    private final Warmup warmup;

    @Autowired
    public StartupApplicationListener(DiscordBotEnvironment discordBotEnvironment, Warmup warmup) {
        this.discordBotEnvironment = discordBotEnvironment;
        this.warmup = warmup;
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationStartedEvent applicationStartedEvent) {
        LOGGER.info("Application started.");
        if (StringUtils.isNotBlank(discordBotEnvironment.getAuthor())) {
            LOGGER.info("Author: {}", discordBotEnvironment.getAuthor());
        }

        if (StringUtils.isNotBlank(discordBotEnvironment.getName())) {
            LOGGER.info("Name: {}", discordBotEnvironment.getName());
        }

        if (StringUtils.isNotBlank(discordBotEnvironment.getVersion())) {
            LOGGER.info("Version: {}", discordBotEnvironment.getVersion());
        }

        warmup.perform();
    }


}
