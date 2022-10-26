/*
 * Copyright (c) 2022 Thibault Helsmoortel.
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

import be.thibaulthelsmoortel.currencyconverterbot.client.stats.payload.CurrencyStat;
import be.thibaulthelsmoortel.currencyconverterbot.client.stats.payload.StatsRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.stats.payload.StatsResponse;
import be.thibaulthelsmoortel.currencyconverterbot.client.stats.service.StatsService;
import be.thibaulthelsmoortel.currencyconverterbot.commands.converters.LowerToUpperCaseConverter;
import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import be.thibaulthelsmoortel.currencyconverterbot.validation.CurrencyIsoCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * @author Thibault Helsmoortel
 */
@RequiredArgsConstructor
@Command(name = "stats", description = "Retrieves stats of a currency.")
@Component
public class StatsCommand extends BotCommand<MessageEmbed> {

    protected static final String ERROR_MESSAGE = "Unable to perform the stats request. Please verify the input parameters and try again. If the issue persists, please make sure to report the issue via the 'issue' command.";

    private static final String HEADER = "Currency stats";

    @SuppressWarnings("unused") // Used through parameter
    @Parameters(description = "ISO code of the currency.", arity = "1", index = "0", converter = LowerToUpperCaseConverter.class)
    @CurrencyIsoCode
    private String isoCode;

    private final StatsService statsService;

    @Override
    public SlashCommandData getSlashCommandData() {
        return Commands.slash("stats", "Retrieves stats of a currency.")
                .addOption(OptionType.STRING, "iso_code", "ISO code of the currency.", true);
    }

    @Override
    public MessageEmbed call() {
        MessageEmbed embed = null;
        validate();

        if (getEvent() instanceof SlashCommandInteractionEvent slashCommandInteractionEvent) {
            var embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(HEADER + " of " + isoCode);

            StatsRequest statsRequest = new StatsRequest();
            statsRequest.setIsoCode(isoCode);

            try {
                StatsResponse response = statsService.getStats(statsRequest);

                embedBuilder.addField("Total conversions with " + isoCode + " as a source",
                        String.valueOf(response.getConversionsWithSource()), true);

                List<CurrencyStat> mostConversionsWithSource = response.getMostConversionsWithSource();

                embedBuilder.addField("Top occurrences",
                        formatOccurrences(mostConversionsWithSource),
                        false);

                embedBuilder.addField("Total conversions with " + isoCode + " as a target",
                        String.valueOf(response.getConversionsWithTarget()), true);

                List<CurrencyStat> mostConversionsWithTarget = response.getMostConversionsWithTarget();

                embedBuilder.addField("Top occurrences",
                        formatOccurrences(mostConversionsWithTarget),
                        false);

                embed = embedBuilder.build();
                slashCommandInteractionEvent.getInteraction().replyEmbeds(embed).queue();
            } catch (WebClientResponseException e) {
                embedBuilder.setDescription(ERROR_MESSAGE);
                embed = embedBuilder.build();
                slashCommandInteractionEvent.getInteraction().replyEmbeds(embed).queue();
            }
        }

        return embed;
    }

    private String formatOccurrences(List<CurrencyStat> currencyStats) {
        final String format = "%-4s- %d";

        return currencyStats.stream()
                .map(stats -> String.format(format, stats.getIsoCode(), stats.getOccurrences()))
                .collect(Collectors.joining("\n"));
    }
}
