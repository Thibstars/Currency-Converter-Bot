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

package be.thibaulthelsmoortel.currencyconverterbot.commands.core;

import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;
import static org.junit.jupiter.params.ParameterizedTest.INDEX_PLACEHOLDER;

import be.thibaulthelsmoortel.currencyconverterbot.BaseTest;
import java.util.stream.Stream;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class MessageChannelOutputStreamTest extends BaseTest {

    private MessageChannelOutputStream messageChannelOutputStream;

    @Mock
    private MessageChannel messageChannel;

    @BeforeEach
    void setUp() {
        messageChannelOutputStream = new MessageChannelOutputStream();
        messageChannelOutputStream.setMessageChannel(messageChannel);

        Mockito.when(messageChannel.sendMessageEmbeds(ArgumentMatchers.any(MessageEmbed.class)))
                .thenReturn(Mockito.mock(MessageCreateAction.class));
    }

    @DisplayName("Should write message to channel.")
    @Test
    void shouldWriteMessageToChannel() {
        String message = "Hello World!";
        EmbedBuilder embedBuilder = new EmbedBuilder();
        StringBuilder descriptionBuilder = embedBuilder.getDescriptionBuilder();
        descriptionBuilder.append(message);

        messageChannelOutputStream.write(message.getBytes(), 0, message.length());

        Mockito.verify(messageChannel).sendMessageEmbeds(embedBuilder.build());
    }

    @DisplayName("Should write char code to channel.")
    @Test
    void shouldWriteCharCodeToChannel() {
        String message = "a";
        int charCode = message.toCharArray()[0];
        messageChannelOutputStream.write(charCode);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        StringBuilder descriptionBuilder = embedBuilder.getDescriptionBuilder();
        descriptionBuilder.append(message);

        Mockito.verify(messageChannel).sendMessageEmbeds(embedBuilder.build());
    }

    @DisplayName("Should not write blank message to channel.")
    @ParameterizedTest(name = INDEX_PLACEHOLDER + ": " + ARGUMENTS_PLACEHOLDER)
    @MethodSource("blankStrings")
    void shouldNotWriteBlankMessageToChannel(String message) {
        messageChannelOutputStream.write(message.getBytes(), 0, message.length());

        Mockito.verifyNoMoreInteractions(messageChannel);
    }

    @DisplayName("Should not write null message to channel.")
    @SuppressWarnings("all")
    @Test
    void shouldNotWriteNullMessageToChannel() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> messageChannelOutputStream.write(null, 0, 0), "Shouldn't be able to pass null.");

        Mockito.verifyNoMoreInteractions(messageChannel);
    }

    static Stream<String> blankStrings() {
        return Stream.of("", "   ", "\t");
    }
}
