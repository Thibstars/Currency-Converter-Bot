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

import be.thibaulthelsmoortel.currencyconverterbot.commands.candidates.ExchangeRateProviderCandidates;
import be.thibaulthelsmoortel.currencyconverterbot.commands.converters.LowerToUpperCaseConverter;
import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import javax.money.UnknownCurrencyException;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * @author Thibault Helsmoortel
 */
@Command(name = "rate", description = "Provides current currency rate.")
@Component
public class RateCommand extends BotCommand {

    @Parameters(description = "ISO code of the currency to lookup.", arity = "1", index = "0", converter = LowerToUpperCaseConverter.class)
    private String isoCode;

    @SuppressWarnings("unused") // Used through option
    @Option(names = {"-c", "--currency"}, paramLabel = "CURRENCY", description = "The base currency iso code.", defaultValue = "EUR", arity = "0..1",
    converter = LowerToUpperCaseConverter.class)
    private String baseCurrencyIsoCode;

    @Option(names = {"-p", "--providers"}, paramLabel = "PROVIDERS", description = "Exchange rate providers. Candidates: ${COMPLETION-CANDIDATES}", arity = "0..*",
        completionCandidates = ExchangeRateProviderCandidates.class, converter = LowerToUpperCaseConverter.class)
    private String[] providers;

    @Override
    public Object call() {
        String message = null;
        if (getEvent() instanceof MessageReceivedEvent) {
            ExchangeRateProvider rateProvider;

            if (providers != null && providers.length > 0) {
                rateProvider = MonetaryConversions.getExchangeRateProvider(providers);
            } else {
                rateProvider = MonetaryConversions.getExchangeRateProvider();
            }

            try {
                if (baseCurrencyIsoCode != null && rateProvider.isAvailable(baseCurrencyIsoCode, isoCode)) {
                    ExchangeRate exchangeRate = rateProvider.getExchangeRate(baseCurrencyIsoCode, isoCode);
                    Money result = Money.of(exchangeRate.getFactor(), exchangeRate.getCurrency());
                    message = result.toString();
                } else {
                    message = "Couldn't find rate for specified ISO code.";
                }
            } catch (UnknownCurrencyException e) {
                message = "Currency ISO code not found.";
            }

            ((MessageReceivedEvent) getEvent()).getChannel().sendMessage(message).queue();
        }

        reset();

        return message;
    }

    // Visible for testing
    void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    private void reset() {
        providers = null;
    }

    // Visible for testing
    void setBaseCurrencyIsoCode(String baseCurrencyIsoCode) {
        this.baseCurrencyIsoCode = baseCurrencyIsoCode;
    }
}
