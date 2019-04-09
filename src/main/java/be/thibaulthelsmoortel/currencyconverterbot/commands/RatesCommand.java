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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * @author Thibault Helsmoortel
 */
@Command(name = "rates", description = "Provides current currency rates.")
@Component
public class RatesCommand extends BotCommand {

    private static final String CURRENCY_PRINT = "Currency";
    private static final String RATE_PRINT = "Rate";
    private static final String SEPARATOR = ":";

    private final RatesParser ratesParser;

    @Autowired
    public RatesCommand(RatesParser ratesParser) {
        this.ratesParser = ratesParser;
    }

    @Override
    public Object call() {
        AtomicReference<String> message = new AtomicReference<>();

        if (getEvent() instanceof MessageReceivedEvent) {
            List<Rate> rates = ratesParser.parse();
            if (rates != null &&!rates.isEmpty()) {
                message.set(CURRENCY_PRINT + SEPARATOR + RATE_PRINT + System.lineSeparator());
                rates.forEach(rate -> message.set(message.get() + rate.toString() + System.lineSeparator()));
            }

            ((MessageReceivedEvent) getEvent()).getChannel().sendMessage(message.get()).queue();
        }

        return message.get();
    }
}
