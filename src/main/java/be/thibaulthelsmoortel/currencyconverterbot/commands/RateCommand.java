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
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * @author Thibault Helsmoortel
 */
@RequiredArgsConstructor
@Command(name = "rate", description = "Provides current currency rate.")
@Component
public class RateCommand extends BotCommand<String> {

    @Parameters(description = "ISO code of the currency to lookup.", arity = "1", index = "0", converter = LowerToUpperCaseConverter.class)
    private String isoCode;

    @SuppressWarnings("unused") // Used through option
    @Option(names = {"-c", "--currency"}, paramLabel = "CURRENCY", description = "The base currency iso code.  Default: ${DEFAULT-VALUE}", defaultValue = "EUR", arity = "0..1",
    converter = LowerToUpperCaseConverter.class)
    private String baseCurrencyIsoCode;

    private final RateService rateService;

    @Override
    public String call() {
        String message = null;
        if (getEvent() instanceof MessageReceivedEvent messageReceivedEvent) {
            RateRequest rateRequest = new RateRequest();
            rateRequest.setBaseIsoCode(baseCurrencyIsoCode);
            rateRequest.setTargetIsoCode(isoCode);

            RateResponse rate = rateService.getRate(rateRequest);

            if (rate != null && rate.getResult() != null) {
                message = "1" + baseCurrencyIsoCode.toUpperCase() + " = " + rate.getResult() + " " + isoCode.toUpperCase();
            } else {
                message = "Unable to perform the rate request. Please verify the input parameters and try again. If the issue persists, please make sure to report the issue via the 'issue' command.";
            }

            messageReceivedEvent.getChannel().sendMessage(message).queue();
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
}
