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

import be.thibaulthelsmoortel.currencyconverterbot.api.model.Rate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

/**
 * @author Thibault Helsmoortel
 */
@Component
public class CurrencyConverter {

    public BigDecimal getConvertedValue(double sourceAmount, Rate sourceRate, Rate targetRate) {
        BigDecimal rate = sourceRate.getValue().multiply(targetRate.getValue());

        if (sourceRate.getValue().compareTo(BigDecimal.ONE) != 0 && targetRate.getValue().compareTo(BigDecimal.ONE) != 0) {
            Rate baseRate = new Rate();
            baseRate.setValue(BigDecimal.ONE);
            BigDecimal interMediaryConversion = convert(sourceAmount, sourceRate, baseRate, sourceRate.getValue());
            Rate intermediaryRate = new Rate();
            intermediaryRate.setValue(interMediaryConversion);

            BigDecimal newRate = convert(sourceAmount, intermediaryRate, targetRate, interMediaryConversion.multiply(targetRate.getValue()));
            return convert(sourceAmount, intermediaryRate, targetRate, newRate);
        } else {
            return convert(sourceAmount, sourceRate, targetRate, rate);
        }
    }

    private BigDecimal convert(double sourceAmount, Rate sourceRate, Rate targetRate, BigDecimal rate) {
        if (sourceRate.getValue().compareTo(targetRate.getValue()) >= 0) {
            return BigDecimal.valueOf(sourceAmount).divide(rate, 5, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.valueOf(sourceAmount).multiply(rate);
        }
    }

}
