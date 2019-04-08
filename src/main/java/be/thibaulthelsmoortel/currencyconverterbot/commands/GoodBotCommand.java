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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * Command thanking the bot for being helpful.
 *
 * @author Thibault Helsmoortel
 */
@Command(name = "good bot", description = "Allows you to thank the bot for being helpful.")
@Component
public class GoodBotCommand extends BotCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodBotCommand.class);

    private static final String THUMBS_UP_EMOJI = "\uD83D\uDC4D";
    private static final String VICTORY_HAND_EMOJI = "\u270C";
    private static final String METAL_EMOJI = "\uD83E\uDD18";
    private static final String SMILING_FACE_WITH_SMILING_EYES_EMOJI = "\uD83D\uDE0A";
    private static final String HUG_EMOJI = "\uD83E\uDD17";
    private static final String FIST_BUMP_EMOJI = "\uD83D\uDC4A";

    private static final List<String> EMOTES =
        Arrays.asList(THUMBS_UP_EMOJI, VICTORY_HAND_EMOJI, METAL_EMOJI, SMILING_FACE_WITH_SMILING_EYES_EMOJI, HUG_EMOJI, FIST_BUMP_EMOJI);

    private Random random;

    public GoodBotCommand() {
        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn("Using fallback random generator.");
            this.random = new Random();
        }
    }

    @Override
    public Object call() {
        String emoji = null;
        if (getEvent() instanceof MessageReceivedEvent) {
            Message message = ((MessageReceivedEvent) getEvent()).getMessage();
            emoji = getRandomEmote();
            message.addReaction(emoji).queue();
        }

        return emoji;
    }

    private String getRandomEmote() {
        return EMOTES.get(random.nextInt(EMOTES.size()));
    }
}
