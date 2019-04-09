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

package be.thibaulthelsmoortel.currencyconverterbot.api.parsers;

import be.thibaulthelsmoortel.currencyconverterbot.api.model.Currency;
import be.thibaulthelsmoortel.currencyconverterbot.api.model.Rate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides means for parsing currency rates.
 *
 * @author Thibault Helsmoortel
 */
@Component
public class RatesParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(RatesParser.class);

    private static final String CUBE = "Cube";
    private static final String TIME = "time";
    private static final String TIME_FORMAT = "Time: %s";
    private static final String CURRENCY = "currency";
    private static final String RATE = "rate";

    private final DocumentParser documentParser;

    @Autowired
    public RatesParser(DocumentParser documentParser) {
        this.documentParser = documentParser;
    }

    public List<Rate> parse() {
        Document document = documentParser.parse();

        LOGGER.debug("Attempting to parse currency rates.");

        NodeList nodeList = null;
        if (document != null) {
            nodeList = document.getElementsByTagName(CUBE);
        }

        List<Rate> rates = new ArrayList<>();

        if (nodeList != null && nodeList.getLength() > 0) {
            LOGGER.debug("Parsing currency rates.");
            IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item).filter(Node::hasAttributes).map(Node::getAttributes)
                .forEach(attributes -> {
                    Node time = attributes.getNamedItem(TIME);
                    if (time != null) {
                        LOGGER.debug("Detected time value: {}.", String.format(TIME_FORMAT, time.getNodeValue()));
                    } else {
                        String currencyString = attributes.getNamedItem(CURRENCY).getNodeValue();
                        String rateString = attributes.getNamedItem(RATE).getNodeValue();

                        Currency currency = new Currency();
                        currency.setIsoCode(currencyString);

                        Rate rate = new Rate();
                        rate.setCurrency(currency);
                        rate.setValue(new BigDecimal(rateString));

                        rates.add(rate);
                    }
                });
        }

        return rates;
    }

}
