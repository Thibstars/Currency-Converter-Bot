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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * @author Thibault Helsmoortel
 */
@Command(name = "rates", description = "Provides current currency rates.")
@Component
public class RatesCommand extends BotCommand {

    private static final String HEADER = "Currency rates";

    private final RatesParser ratesParser;

    @Autowired
    public RatesCommand(RatesParser ratesParser) {
        this.ratesParser = ratesParser;
    }

    @Override
    public Object call() {
        MessageEmbed embed = null;

        if (getEvent() instanceof MessageReceivedEvent) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(HEADER);

            List<Rate> rates = ratesParser.parse();
            if (rates != null && !rates.isEmpty()) {
                rates.forEach(rate -> embedBuilder.addField(rate.getCurrency().getIsoCode(), rate.getValue().toPlainString(), true));
            }

            embed = embedBuilder.build();
            ((MessageReceivedEvent) getEvent()).getChannel().sendMessage(embed).queue();
        }

        return embed;
    }
}
