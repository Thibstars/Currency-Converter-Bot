/*
 * Copyright (c) 2022 Thibault Helsmoortel.
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

import be.thibaulthelsmoortel.currencyconverterbot.client.stats.payload.CurrencyStat;
import be.thibaulthelsmoortel.currencyconverterbot.client.stats.payload.StatsRequest;
import be.thibaulthelsmoortel.currencyconverterbot.client.stats.payload.StatsResponse;
import be.thibaulthelsmoortel.currencyconverterbot.client.stats.service.StatsService;
import java.lang.reflect.Field;
import java.util.List;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * @author Thibault Helsmoortel
 */
class StatsCommandTest extends CommandBaseTest {

    @InjectMocks
    private StatsCommand statsCommand;

    @Mock
    private StatsService statsService;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        statsCommand.setEvent(slashCommandInteractionEvent);
        try {
            Field isoCodeField = statsCommand.getClass().getDeclaredField("isoCode");
            isoCodeField.setAccessible(true);
            isoCodeField.set(statsCommand, "EUR");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("Should send stats message.")
    @Test
    void shouldSendStatsMessage() {
        String isoCode = "EUR";
        StatsRequest statsRequest = new StatsRequest();
        statsRequest.setIsoCode(isoCode);

        long conversionsWithSource = 5L;
        List<CurrencyStat> mostConversionsWithSource = List.of(
                CurrencyStat.builder()
                        .isoCode("USD")
                        .occurrences(5L)
                        .build()
        );
        long conversionsWithTarget = 3L;
        List<CurrencyStat> mostConversionsWithTarget = List.of(
                CurrencyStat.builder()
                        .isoCode("UYU")
                        .occurrences(2L)
                        .build(),
                CurrencyStat.builder()
                        .isoCode("JPY")
                        .occurrences(1L)
                        .build()
        );

        StatsResponse statsResponse = new StatsResponse();
        statsResponse.setIsoCode(isoCode);
        statsResponse.setConversionsWithSource(conversionsWithSource);
        statsResponse.setMostConversionsWithSource(mostConversionsWithSource);
        statsResponse.setConversionsWithTarget(conversionsWithTarget);
        statsResponse.setMostConversionsWithTarget(mostConversionsWithTarget);

        Mockito.when(statsService.getStats(statsRequest)).thenReturn(statsResponse);

        Mockito.when(messageChannelUnion.sendMessageEmbeds(ArgumentMatchers.any(MessageEmbed.class)))
                .thenReturn(Mockito.mock(MessageCreateAction.class));

        MessageEmbed embed = statsCommand.call();

        Assertions.assertNotNull(embed, "Message should not be null.");
        Assertions.assertTrue(StringUtils.isNotBlank(embed.getTitle()), "Title should not be empty.");

        List<MessageEmbed.Field> embedFields = embed.getFields();
        Assertions.assertEquals(4, embedFields.size(), "Embed fields size must be correct.");
        Assertions.assertEquals("Total conversions with EUR as a source",
                embedFields.get(0).getName(),
                "Source title must be correct."
        );
        Assertions.assertEquals(String.valueOf(conversionsWithSource),
                embedFields.get(0).getValue(),
                "Conversions value must be correct."
        );
        Assertions.assertEquals("Top occurrences",
                embedFields.get(1).getName(),
                "Stats title must be correct."
        );
        Assertions.assertEquals("Total conversions with EUR as a target",
                embedFields.get(2).getName(),
                "Target title must be correct."
        );
        Assertions.assertEquals(String.valueOf(conversionsWithTarget),
                embedFields.get(2).getValue(),
                "Conversions value must be correct."
        );
        Assertions.assertEquals("Top occurrences",
                embedFields.get(3).getName(),
                "Stats title must be correct."
        );
    }

    @DisplayName("Should handle WebClientResponseException.")
    @Test
    void shouldHandleWebClientResponseException() {
        StatsRequest statsRequest = new StatsRequest();
        statsRequest.setIsoCode("EUR");

        Mockito.when(statsService.getStats(statsRequest)).thenThrow(WebClientResponseException.class);

        Mockito.when(messageChannelUnion.sendMessageEmbeds(ArgumentMatchers.any(MessageEmbed.class)))
                .thenReturn(Mockito.mock(MessageCreateAction.class));

        MessageEmbed embed = statsCommand.call();

        Assertions.assertNotNull(embed, "Message should not be null.");
        Assertions.assertEquals(StatsCommand.ERROR_MESSAGE, embed.getDescription(), "Message should be correct.");
        verifyOneMessageReplied(embed);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        verifyDoNotProcessEvent(statsCommand, Mockito.mock(Event.class));
    }
}