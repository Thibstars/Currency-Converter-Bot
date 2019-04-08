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

import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import picocli.CommandLine.Command;

/**
 * @author Thibault Helsmoortel
 */
@Command(name = "rates", description = "Provides current currency rates.")
@Component
public class RatesCommand extends BotCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(RatesCommand.class);

    private static final String ECB_XML_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private static final String CUBE = "Cube";
    private static final String TIME = "time";
    private static final String CURRENCY = "currency";
    private static final String RATE = "rate";
    private static final String CURRENCY_PRINT = "Currency";
    private static final String RATE_PRINT = "Rate";
    private static final String TIME_FORMAT = "Time: %s";
    private static final String SEPARATOR = "|";
    private static final String FORMAT = "%-8s %-1s %-4s %n";

    @Override
    public Object call() throws Exception {
        AtomicReference<String> message = new AtomicReference<>();

        if (getEvent() instanceof MessageReceivedEvent) {
            URL xmlURL = new URL(ECB_XML_URL);
            try (InputStream xml = xmlURL.openStream()) {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(xml);

                NodeList nodeList = document.getElementsByTagName(CUBE);

                if (nodeList.getLength() > 0) {
                    message.set(String.format(FORMAT, CURRENCY_PRINT, SEPARATOR, RATE_PRINT));
                }

                IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item).filter(Node::hasAttributes).map(Node::getAttributes)
                    .forEach(attributes -> {
                        Node time = attributes.getNamedItem(TIME);
                        if (time != null) {
                            LOGGER.debug("Search performed on: {}.", String.format(TIME_FORMAT, time.getNodeValue()));
                        } else {
                            String currency = attributes.getNamedItem(CURRENCY).getNodeValue();
                            String rate = attributes.getNamedItem(RATE).getNodeValue();

                            message.set(message.get() + String.format(FORMAT, currency, SEPARATOR, rate));
                        }
                    });

                ((MessageReceivedEvent) getEvent()).getChannel().sendMessage(message.get()).queue();
            }
        }

        return message.get();
    }
}
