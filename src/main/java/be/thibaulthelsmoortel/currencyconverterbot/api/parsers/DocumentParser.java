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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Provides means for parsing a currency rates document from ECB.
 *
 * @author Thibault Helsmoortel
 */
@Component
public class DocumentParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentParser.class);

    private static final String ECB_XML_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    Document parse() {
        LOGGER.debug("Attempting to parse currency document.");
        URL xmlURL;
        try {
            xmlURL = new URL(ECB_XML_URL);
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage(), e);

            return null;
        }

        try (InputStream xml = xmlURL.openStream()) {
            LOGGER.debug("Preparing document parsing.");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            LOGGER.debug("Parsing document.");
            return documentBuilder.parse(xml);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            LOGGER.error(e.getMessage(), e);

            return null;
        }
    }
}
