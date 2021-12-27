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

import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import java.util.Collection;
import java.util.NoSuchElementException;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * @author Thibault Helsmoortel
 */
@Command(name = "convert", description = "Converts one currency value to another.")
@Component
public class ConvertCommand extends BotCommand<String> {

    @Parameters(description = "Value of the currency to convert.", arity = "1", index = "0")
    private double sourceAmount;
    @Parameters(description = "ISO code of the source currency.", arity = "1", index = "1")
    private String sourceIsoCode;
    @Parameters(description = "ISO code of the target currency.", arity = "1", index = "2")
    private String targetIsoCode;

    @Override
    public String call() {
        String message = null;

        if (getEvent() instanceof MessageReceivedEvent messageReceivedEvent) {
            try {
                Collection<CurrencyUnit> currencies = Monetary.getCurrencies();
                CurrencyUnit sourceUnit = currencies.stream()
                    .filter(unit -> unit.getCurrencyCode().equalsIgnoreCase(sourceIsoCode))
                    .findFirst()
                    .orElseThrow();

                CurrencyUnit targetUnit =
                    currencies.stream()
                        .filter(unit -> unit.getCurrencyCode().equalsIgnoreCase(targetIsoCode))
                        .findFirst()
                        .orElseThrow();

                CurrencyConversion conversion = MonetaryConversions.getConversion(targetUnit);

                MonetaryAmount monetarySourceAmount = Money.of(sourceAmount, sourceUnit);

                message = sourceAmount + " " + sourceIsoCode.toUpperCase() + " = " + monetarySourceAmount.with(conversion).toString();
            } catch (NoSuchElementException e) {
                message = "Input parameters not recognized.";
            }

            messageReceivedEvent.getChannel().sendMessage(message).queue();
        }

        return message;
    }

    // Visible for testing
    void setSourceAmount(double sourceAmount) {
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
