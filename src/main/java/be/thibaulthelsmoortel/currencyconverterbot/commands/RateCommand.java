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

import be.thibaulthelsmoortel.currencyconverterbot.client.rate.payload.RateRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.rate.payload.RateResponse;
import be.thibaulthelsmoortel.currencyconverterbot.client.rate.service.RateService;
import be.thibaulthelsmoortel.currencyconverterbot.commands.converters.LowerToUpperCaseConverter;
import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import be.thibaulthelsmoortel.currencyconverterbot.validation.CurrencyIsoCode;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
@Command(name = "rate", description = "Provides current currency rate.")
@Component
public class RateCommand extends BotCommand<String> {

    protected static final String ERROR_MESSAGE = "Unable to perform the rate request. Please verify the input parameters and try again. If the issue persists, please make sure to report the issue via the 'issue' command.";

    @Parameters(description = "ISO code of the currency to lookup.", arity = "1", index = "0", converter = LowerToUpperCaseConverter.class)
    @NotNull
    @CurrencyIsoCode
    private String isoCode;

    @SuppressWarnings("unused") // Used through option
    @Parameters(description = "The base currency iso code.  Default: ${DEFAULT-VALUE}", defaultValue = "EUR", arity = "0..1", converter = LowerToUpperCaseConverter.class)
    @CurrencyIsoCode
    private String baseCurrencyIsoCode;

    private final RateService rateService;

    @Override
    public String call() {
        String message = null;
        validate();

        if (getEvent() instanceof SlashCommandInteractionEvent slashCommandInteractionEvent) {
            RateRequest rateRequest = new RateRequest();
            rateRequest.setBaseIsoCode(baseCurrencyIsoCode);
            rateRequest.setTargetIsoCode(isoCode);

            try {
                RateResponse rate = rateService.getRate(rateRequest);

                if (rate != null && rate.getResult() != null) {
                    message = "1 " + baseCurrencyIsoCode.toUpperCase() + " = " + rate.getResult() + " " + isoCode.toUpperCase();
                } else {
                    message = ERROR_MESSAGE;
                }

                slashCommandInteractionEvent.getInteraction().reply(message).queue();
            } catch (WebClientResponseException e) {
                message = ERROR_MESSAGE;
                slashCommandInteractionEvent.getInteraction().reply(message).queue();
            }
        }

        return message;
    }

    // Visible for testing
    void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    // Visible for testing
    @SuppressWarnings("all")
    void setBaseCurrencyIsoCode(String baseCurrencyIsoCode) {
        this.baseCurrencyIsoCode = baseCurrencyIsoCode;
    }

    @Override
    public SlashCommandData getSlashCommandData() {
        return Commands.slash("rate", "Provides current currency rate.")
                .addOption(OptionType.STRING, "iso_code", "ISO code of the currency to lookup.", true)
                .addOption(OptionType.STRING, "base_iso_code", "The base currency iso code.", false);
    }
}
