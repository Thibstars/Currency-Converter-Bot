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
import java.util.NoSuchElementException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * @author Thibault Helsmoortel
 */
@Command(name = "rate", description = "Provides current currency rate.")
@Component
public class RateCommand extends BotCommand {

    @Parameters(description = "ISO code of the currency to lookup.", arity = "1", index = "0")
    private String isoCode;

    private final RatesParser ratesParser;

    @Autowired
    public RateCommand(RatesParser ratesParser) {
        this.ratesParser = ratesParser;
    }

    @Override
    public Object call() {
        String message = null;
        if (getEvent() instanceof MessageReceivedEvent) {
            Rate rate;
            try {
                rate = ratesParser.parse(isoCode);
            } catch (NoSuchElementException e) {
                message = "Currency ISO code not found.";
                ((MessageReceivedEvent) getEvent()).getChannel().sendMessage(message).queue();

                return message;
            }

            if (rate != null) {
                message = rate.toString();
            } else {
                message = "Couldn't find rate for specified ISO code.";
            }

            ((MessageReceivedEvent) getEvent()).getChannel().sendMessage(message).queue();
        }

        return message;
    }

    // Visible for testing
    void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }
}
