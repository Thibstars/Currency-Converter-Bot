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

package be.thibaulthelsmoortel.currencyconverterbot.application;

import java.util.Collection;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class warming up the application upon startup. This is useful since Moneta is fetching quite some data on the first performed conversion.
 *
 * @author Thibault Helsmoortel
 */
@Component
class Warmup {

    private static final Logger LOGGER = LoggerFactory.getLogger(Warmup.class);

    @Value("${bot.warmup}")
    private boolean perform;

    private boolean performed;

    Warmup() {
        this.performed = false;
    }

    public void perform() {
        if (perform && !performed) {
            try {
                LOGGER.info("Performing warmup...");

                Collection<CurrencyUnit> currencies = Monetary.getCurrencies();
                String sourceIsoCode = "eur";
                CurrencyUnit sourceUnit = currencies.stream()
                    .filter(unit -> unit.getCurrencyCode().equalsIgnoreCase(sourceIsoCode))
                    .findFirst()
                    .orElseThrow();

                CurrencyUnit targetUnit =
                    currencies.stream()
                        .filter(unit -> {
                            String targetIsoCode = "usd";
                            return unit.getCurrencyCode().equalsIgnoreCase(targetIsoCode);
                        })
                        .findFirst()
                        .orElseThrow();

                CurrencyConversion conversion = MonetaryConversions.getConversion(targetUnit);

                int sourceAmount = 1;
                MonetaryAmount monetarySourceAmount = Money.of(sourceAmount, sourceUnit);
                String conversionResult = sourceAmount + " " + sourceIsoCode.toUpperCase() + " = " + monetarySourceAmount.with(conversion).toString();

                this.performed = true;
                LOGGER.info("Performed warmup with example conversion: {}", conversionResult);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public boolean isPerformed() {
        return performed;
    }
}
