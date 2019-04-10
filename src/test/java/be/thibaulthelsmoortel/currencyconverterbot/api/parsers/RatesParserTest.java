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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.thibaulthelsmoortel.currencyconverterbot.api.model.Currency;
import be.thibaulthelsmoortel.currencyconverterbot.api.model.Rate;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Thibault Helsmoortel
 */
class RatesParserTest {

    private static final String CUBE = "Cube";
    private static final String CURRENCY = "currency";
    private static final String RATE = "rate";
    private static final String TIME = "time";

    private RatesParser ratesParser;
    private Document document;

    @BeforeEach
    void setUp() throws ParserConfigurationException {
        DocumentParser documentParser = mock(DocumentParser.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        this.document = builder.newDocument();
        this.ratesParser = new RatesParser(documentParser);

        Element rootCube = document.createElement(CUBE);
        Element timeCube = createTimeCube();
        appendRateCube(timeCube, "USD", "1.1246");
        appendRateCube(timeCube, "JPY", "125.36");
        rootCube.appendChild(timeCube);
        document.appendChild(rootCube);

        when(documentParser.parse()).thenReturn(document);
    }

    @NotNull
    private Element createTimeCube() {
        Element timeCube = document.createElement(CUBE);
        timeCube.setAttribute(TIME, "2019-04-08");
        return timeCube;
    }

    private void appendRateCube(Element timeCube, String isoCode, String rate) {
        Element rateCube = document.createElement(CUBE);
        rateCube.setAttribute(CURRENCY, isoCode);
        rateCube.setAttribute(RATE, rate);
        timeCube.appendChild(rateCube);
    }

    @DisplayName("Should parse rates.")
    @Test
    void shouldParseRates() {
        List<Rate> rates = ratesParser.parse();

        Assertions.assertNotNull(rates, "Rates must not be null.");
        Assertions.assertFalse(rates.isEmpty(), "Rates must not be empty.");

        Assertions.assertTrue(rates.stream().map(Rate::getCurrency).map(Currency::getIsoCode).anyMatch("USD"::equals), "Rates must contain USD.");
        Assertions.assertTrue(rates.stream().map(Rate::getCurrency).map(Currency::getIsoCode).anyMatch("JPY"::equals), "Rates must contain JPY.");

        rates.forEach(rate -> {
            Assertions.assertNotNull(rate.getCurrency(), "Currency must not be null.");
            Assertions.assertNotNull(rate.getValue(), "Rate must not be null.");
        });
    }

    @DisplayName("Should parse rate.")
    @Test
    void shouldParseRate() {
        Rate rate = ratesParser.parse("USD");

        Assertions.assertNotNull(rate, "Rate must not be null.");
        Assertions.assertNotNull(rate.getCurrency(), "Currency must not be null.");
        Assertions.assertNotNull(rate.getValue(), "Rate must not be null.");
        Assertions.assertEquals("USD", rate.getCurrency().getIsoCode(), "ISO codes must match.");
    }

    @DisplayName("Should not parse blank ISO code.")
    @Test
    void shouldNotParseBlankIsoCode() {
        Rate rate = ratesParser.parse(" ");

        Assertions.assertNull(rate, "Rate must be null.");
    }

    @DisplayName("Should not parse ISO code that isn't present.")
    @Test
    void shouldNotParseUnavailableIsoCode() {
        Assertions.assertThrows(NoSuchElementException.class, () -> ratesParser.parse("KFC"));
    }
}
