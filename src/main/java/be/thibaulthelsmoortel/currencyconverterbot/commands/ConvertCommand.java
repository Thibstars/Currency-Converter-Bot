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

import be.thibaulthelsmoortel.currencyconverterbot.client.conversion.payload.ConversionRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.conversion.payload.ConversionResponse;
import be.thibaulthelsmoortel.currencyconverterbot.client.conversion.service.ConversionService;
import be.thibaulthelsmoortel.currencyconverterbot.commands.converters.LowerToUpperCaseConverter;
import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import be.thibaulthelsmoortel.currencyconverterbot.validation.CurrencyIsoCode;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * @author Thibault Helsmoortel
 */
@RequiredArgsConstructor
@Command(name = "convert", description = "Converts one currency value to another.")
@Component
public class ConvertCommand extends BotCommand<String> {

    protected static final String ERROR_MESSAGE = "Unable to perform the conversion request. Please verify the input parameters and try again. If the issue persists, please make sure to report the issue via the 'issue' command.";

    @Parameters(description = "Value of the currency to convert.", arity = "1", index = "0")
    @NotNull
    private BigDecimal sourceAmount;
    @Parameters(description = "ISO code of the source currency.", arity = "1", index = "1", converter = LowerToUpperCaseConverter.class)
    @CurrencyIsoCode
    private String sourceIsoCode;
    @Parameters(description = "ISO code of the target currency.", arity = "1", index = "2", converter = LowerToUpperCaseConverter.class)
    @CurrencyIsoCode
    private String targetIsoCode;

    private final ConversionService conversionService;

    @Override
    public String call() {
        String message = null;
        validate();

        if (getEvent() instanceof MessageReceivedEvent messageReceivedEvent) {
            ConversionRequest conversionRequest = new ConversionRequest();
            conversionRequest.setSourceAmount(sourceAmount);
            conversionRequest.setSourceIsoCode(sourceIsoCode);
            conversionRequest.setTargetIsoCode(targetIsoCode);

            try {
                ConversionResponse conversion = conversionService.getConversion(conversionRequest);

                if (conversion != null && conversion.getResult() != null) {
                    message = sourceAmount + " " + sourceIsoCode.toUpperCase() + " = " + conversion.getResult() + " " + targetIsoCode.toUpperCase();
                } else {
                    message = ERROR_MESSAGE;
                }

                messageReceivedEvent.getChannel().sendMessage(message).queue();
            } catch (WebClientResponseException e) {
                message = ERROR_MESSAGE;
                messageReceivedEvent.getChannel().sendMessage(message).queue();
            }
        }

        return message;
    }

    // Visible for testing
    void setSourceAmount(BigDecimal sourceAmount) {
        this.sourceAmount = sourceAmount;
    }

    // Visible for testing
    void setSourceIsoCode(String sourceIsoCode) {
        this.sourceIsoCode = sourceIsoCode;
    }

    // Visible for testing
    void setTargetIsoCode(String targetIsoCode) {
        this.targetIsoCode = targetIsoCode;
    }
}
