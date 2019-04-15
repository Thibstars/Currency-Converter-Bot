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

package be.thibaulthelsmoortel.currencyconverterbot.api.conversion;

import be.thibaulthelsmoortel.currencyconverterbot.api.model.Currency;
import be.thibaulthelsmoortel.currencyconverterbot.api.model.Rate;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Thibault Helsmoortel
 */
class CurrencyConverterTest {

    private CurrencyConverter currencyConverter;

    @BeforeEach
    void setUp() {
        this.currencyConverter = new CurrencyConverter();
    }

    @DisplayName("Should convert USD to EUR")
    @Test
    void shouldConvertUSDToEUR() {
        BigDecimal usdRate = new BigDecimal("0.753");
        Rate usd = createRate("USD", usdRate);
        Rate eur = createBaseRate();

        BigDecimal convertedValue = currencyConverter.getConvertedValue(1, usd, eur).stripTrailingZeros();
        BigDecimal expected = usdRate;
        Assertions.assertEquals(expected, convertedValue, "Converted value must be correct.");

        double customSourceAmount = 37.43;
        convertedValue = currencyConverter.getConvertedValue(customSourceAmount, usd, eur).stripTrailingZeros();
        expected = BigDecimal.valueOf(customSourceAmount).multiply(usdRate);
        Assertions.assertEquals(expected, convertedValue, "Converted value must be correct.");
    }

    @DisplayName("Should convert EUR to USD")
    @Test
    void shouldConvertEURtoUSD() {
        BigDecimal usdRate = new BigDecimal("0.753");
        Rate usd = createRate("USD", usdRate);
        Rate eur = createBaseRate();

        BigDecimal convertedValue = currencyConverter.getConvertedValue(1, eur, usd).stripTrailingZeros();
        BigDecimal expected = eur.getValue().divide(usdRate, 5, RoundingMode.HALF_UP);
        Assertions.assertEquals(expected, convertedValue, "Converted value must be correct.");

        double customSourceAmount = 37.43;
        convertedValue = currencyConverter.getConvertedValue(customSourceAmount, eur, usd).stripTrailingZeros();
        expected = BigDecimal.valueOf(customSourceAmount).divide(usdRate, 5, RoundingMode.HALF_UP);
        Assertions.assertEquals(expected, convertedValue, "Converted value must be correct.");
    }

    @DisplayName("Should convert rates different from one.")
    @Test
    void shouldConvertRatesDifferentFromOne() {
        BigDecimal usdRate = new BigDecimal("0.753");
        Rate usd = createRate("USD", usdRate);
        BigDecimal cadRate = new BigDecimal("1.25");
        Rate cad = createRate("CAD", cadRate);
        BigDecimal usdToCadRate = new BigDecimal("0.94125");

        BigDecimal convertedValue = currencyConverter.getConvertedValue(1, usd, cad).stripTrailingZeros();
        BigDecimal expected = usdToCadRate;
        Assertions.assertEquals(expected, convertedValue, "Converted value must be correct.");

        double customSourceAmount = 37.43;
        convertedValue = currencyConverter.getConvertedValue(customSourceAmount, usd, cad).stripTrailingZeros();
        expected = BigDecimal.valueOf(customSourceAmount).multiply(usdToCadRate);
        BigDecimal subtract = expected.subtract(convertedValue);
        subtract = subtract.setScale(5, RoundingMode.HALF_UP); // Allow minor fault margin
        Assertions.assertTrue(subtract.compareTo(new BigDecimal("0.0001")) <= 0, "Converted value must be correct.");
    }

    private Rate createRate(String iso, BigDecimal value) {
        Rate rate = new Rate();
        Currency currency = new Currency();
        currency.setIsoCode(iso);
        rate.setCurrency(currency);
        rate.setValue(value);

        return rate;
    }

    private Rate createBaseRate() {
        return createRate("EUR", BigDecimal.ONE);
    }

}
