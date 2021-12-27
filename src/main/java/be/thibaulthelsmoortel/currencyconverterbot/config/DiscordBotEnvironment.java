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

package be.thibaulthelsmoortel.currencyconverterbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Discord bot environment properties.
 *
 * @author Thibault Helsmoortel
 */
@ConfigurationProperties(prefix = "bot")
public class DiscordBotEnvironment {

    private String token;

    private String author;

    private String name;

    private String description;

    private String version;

    private String commandPrefix;

    private boolean processBotMessages;

    private String dblToken;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public boolean isProcessBotMessages() {
        return processBotMessages;
    }

    public void setProcessBotMessages(boolean processBotMessages) {
        this.processBotMessages = processBotMessages;
    }

    public String getDblToken() {
        return dblToken;
    }

    public void setDblToken(String dblToken) {
        this.dblToken = dblToken;
    }
}
