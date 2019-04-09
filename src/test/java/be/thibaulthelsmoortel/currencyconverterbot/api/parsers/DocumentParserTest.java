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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

/**
 * @author Thibault Helsmoortel
 */
class DocumentParserTest {

    private DocumentParser documentParser;

    @BeforeEach
    void setUp() {
        this.documentParser = new DocumentParser();
    }

    @DisplayName("Should parse document.")
    @Test
    void shouldParseDocument() {
        Document document = documentParser.parse();

        Assertions.assertNotNull(document, "Document must not be null.");
        Assertions.assertNotNull(document.getChildNodes(), "Nodes must be parsed.");
        Assertions.assertTrue(document.getChildNodes().getLength() > 0, "Nodes must not be empty.");
    }
}
