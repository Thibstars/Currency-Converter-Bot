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

package be.thibaulthelsmoortel.currencyconverterbot.commands;

import be.thibaulthelsmoortel.currencyconverterbot.api.model.Rate;
import be.thibaulthelsmoortel.currencyconverterbot.api.parsers.RatesParser;
import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import java.math.BigDecimal;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * @author Thibault Helsmoortel
 */
@Command(name = "convert", description = "Converts one currency value to another.")
@Component
public class ConvertCommand extends BotCommand {

    private final RatesParser ratesParser;
    @Parameters(description = "Value of the currency to convert.", arity = "1", index = "0")
    private double sourceAmount;
    @Parameters(description = "ISO code of the source currency.", arity = "1", index = "1")
    private String sourceIsoCode;
    @Parameters(description = "ISO code of the target currency.", arity = "1", index = "2")
    private String targetIsoCode;

    @Autowired
    public ConvertCommand(RatesParser ratesParser) {
        this.ratesParser = ratesParser;
    }

    @Override
    public Object call() {
        String message = null;

        if (getEvent() instanceof MessageReceivedEvent) {
            Rate sourceRate = ratesParser.parse(sourceIsoCode);
            Rate targetRate = ratesParser.parse(targetIsoCode);

            BigDecimal result = getConvertedValue(sourceRate, targetRate);
            message = String.format("%s %s", result.toPlainString(), targetRate.getCurrency().getIsoCode());

            ((MessageReceivedEvent) getEvent()).getChannel().sendMessage(message).queue();
        }

        return message;
    }

    private BigDecimal getConvertedValue(Rate sourceRate, Rate targetRate) {
        BigDecimal rate = sourceRate.getValue().multiply(targetRate.getValue());

        // TODO: 2019-04-10 fix currency conversion
        return BigDecimal.valueOf(sourceAmount).multiply(rate);
    }
}
