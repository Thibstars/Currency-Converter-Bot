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

import be.thibaulthelsmoortel.currencyconverterbot.commands.candidates.PermissionCandidates;
import be.thibaulthelsmoortel.currencyconverterbot.commands.converters.PermissionConverter;
import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * Command providing an invitation url for the bot.
 *
 * @author Thibault Helsmoortel
 */
@Command(name = "invite", description = "Provides an invitation url for the bot.")
@Component
public class InviteCommand extends BotCommand<String> {

    @Parameters(paramLabel = "PERMISSION", description = "Target bot permissions. Candidates: ${COMPLETION-CANDIDATES}", arity = "0..*",
        converter = PermissionConverter.class, completionCandidates = PermissionCandidates.class)
    private Permission[] permissions;

    @Override
    public String call() {
        String message = null;
        if (getEvent() instanceof SlashCommandInteractionEvent slashCommandInteractionEvent) {
            var jda = getEvent().getJDA();

            if (permissions != null && permissions.length > 0) {
                message = jda.getInviteUrl(permissions);
            } else {
                message = jda.getInviteUrl(Permission.EMPTY_PERMISSIONS);
            }

            slashCommandInteractionEvent.getInteraction().reply(message).queue();
        }

        reset();

        return message;
    }

    // Visible for testing
    void setPermissions(Permission[] permissions) {
        this.permissions = permissions;
    }

    private void reset() {
        permissions = null;
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        return Commands.slash("invite", "Provides an invitation url for the bot.")
                .addOption(OptionType.STRING, "permissions", "Target bot permissions.", false);
    }
}
