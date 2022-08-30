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

import be.thibaulthelsmoortel.currencyconverterbot.commands.core.CommandExecutor;
import be.thibaulthelsmoortel.currencyconverterbot.config.DiscordBotEnvironment;
import be.thibaulthelsmoortel.currencyconverterbot.exceptions.MissingTokenException;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.discordbots.api.client.DiscordBotListAPI;
import org.discordbots.api.client.DiscordBotListAPI.Builder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Component running the discord bot.
 *
 * @author Thibault Helsmoortel
 */
@Component
@Slf4j
public class DiscordBotRunner extends ListenerAdapter implements CommandLineRunner {

    private final DiscordBotEnvironment discordBotEnvironment;
    private final CommandExecutor commandExecutor;
    private final CommandRegister commandRegister;

    private DiscordBotListAPI dblApi;

    @Autowired
    public DiscordBotRunner(DiscordBotEnvironment discordBotEnvironment, CommandExecutor commandExecutor,
            CommandRegister commandRegister) {
        this.discordBotEnvironment = discordBotEnvironment;
        this.commandExecutor = commandExecutor;
        this.commandRegister = commandRegister;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandString = event.getCommandString();
        if (StringUtils.isNotBlank(commandString) && processMessage(event)) {
            handleMessage(event, commandString);
        }
    }

    private boolean processMessage(SlashCommandInteractionEvent event) {
        return (discordBotEnvironment.isProcessBotMessages() && event.getInteraction().getUser().isBot()) || !event.getInteraction().getUser().isBot();
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        super.onGuildJoin(event);

        log.info("Joined guild. Current amount of connected guilds: {}.", event.getJDA().getGuilds().size());

        updateServerCount(event.getJDA());
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        super.onGuildLeave(event);

        log.info("Left guild. Current amount of connected guilds: {}.", event.getJDA().getGuilds().size());

        updateServerCount(event.getJDA());
    }

    private void updateServerCount(JDA jda) {
        dblApi.setStats(jda.getGuilds().size());
    }

    private void handleMessage(SlashCommandInteractionEvent event, String msg) {
        if (msg.startsWith(discordBotEnvironment.getCommandPrefix())) {
            event.getChannel().sendTyping().queue();

            var parsedMessage = msg.substring(discordBotEnvironment.getCommandPrefix().length());

            commandExecutor.tryExecute(event, parsedMessage);
        }
    }

    @Override
    public void run(String... args) {
        String token;
        if (StringUtils.isNotBlank(discordBotEnvironment.getToken())) {
            token = discordBotEnvironment.getToken();
        } else {
            // Take token as first run arg (for example for when running from docker with an ENV variable)
            if (args != null && args.length > 0) {
                token = args[0];
            } else {
                token = null;
            }
        }
        String dblToken;
        if (StringUtils.isNotBlank(discordBotEnvironment.getDblToken())) {
            dblToken = discordBotEnvironment.getDblToken();
        } else {
            if (args != null && args.length > 1) {
                dblToken = args[1];
            } else {
                dblToken = null;
            }
        }

        if (StringUtils.isBlank(token)) {
            throw new MissingTokenException();
        }

        try {
            var jda = JDABuilder.createDefault(token)
                .addEventListeners(this)
                .build()
                .awaitReady();

            commandRegister.registerCommands(jda);

            this.dblApi = new Builder()
                .token(dblToken)
                .botId(jda.getSelfUser().getId())
                .build();
        } catch (InterruptedException e) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    // Visible for testing
    void setDblApi(DiscordBotListAPI dblApi) {
        this.dblApi = dblApi;
    }
}
