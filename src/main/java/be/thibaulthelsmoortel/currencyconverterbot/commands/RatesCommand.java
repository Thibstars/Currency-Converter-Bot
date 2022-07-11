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

import be.thibaulthelsmoortel.currencyconverterbot.client.rate.payload.RateResponse;
import be.thibaulthelsmoortel.currencyconverterbot.client.rates.payload.RatesRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.rates.payload.RatesResponse;
import be.thibaulthelsmoortel.currencyconverterbot.client.rates.service.RatesService;
import be.thibaulthelsmoortel.currencyconverterbot.commands.converters.LowerToUpperCaseConverter;
import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * @author Thibault Helsmoortel
 */
@RequiredArgsConstructor
@Command(name = "rates", description = "Provides current currency rates.")
@Component
public class RatesCommand extends BotCommand<MessageEmbed> {

    private static final String HEADER = "Currency rates";

    @SuppressWarnings("unused") // Used through option
    @Option(names = {"-c",
            "--currency"}, paramLabel = "CURRENCY", description = "The base currency iso code.", defaultValue = "EUR", arity = "0..1",
            converter = LowerToUpperCaseConverter.class)
    private String baseCurrencyIsoCode;

    private final RatesService ratesService;

    @Override
    public MessageEmbed call() {
        MessageEmbed embed = null;

        if (getEvent() instanceof MessageReceivedEvent messageReceivedEvent) {
            var embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(HEADER);

            RatesRequest ratesRequest = new RatesRequest();
            ratesRequest.setBaseIsoCode(baseCurrencyIsoCode);

            RatesResponse response = ratesService.getRates(ratesRequest);
            List<RateResponse> rates = response.getRates()
                    .stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(RateResponse::getResult))
                    .toList();

            rates.forEach(rate -> embedBuilder.addField(rate.getTargetIsoCode(), rate.getResult().toPlainString(), true));

            embed = embedBuilder.build();
            messageReceivedEvent.getChannel().sendMessageEmbeds(embed).queue();
        }

        return embed;
    }

    // Visible for testing
    @SuppressWarnings("all")
    void setBaseCurrencyIsoCode(String baseCurrencyIsoCode) {
        this.baseCurrencyIsoCode = baseCurrencyIsoCode;
    }
}
