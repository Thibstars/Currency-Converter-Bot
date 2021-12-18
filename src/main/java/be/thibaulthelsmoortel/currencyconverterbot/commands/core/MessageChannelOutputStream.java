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

import java.io.OutputStream;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * OutputStream able to write to a set message channel.
 *
 * @author Thibault Helsmoortel
 */
@Component
public class MessageChannelOutputStream extends OutputStream {

    private MessageChannel messageChannel;

    @Override
    public void write(int b) {
        // It's tempting to use writer.write((char) b), but that may get the encoding wrong
        // This is inefficient, but it works
        write(new byte[] {(byte) b}, 0, 1);
    }

    @SuppressWarnings("all") // hard null check
    @Override
    public void write(byte @NotNull [] b, int off, int len) {
        if (b == null) {
            throw new IllegalArgumentException();
        }

        var content = new String(b, off, len);
        if (StringUtils.isNotBlank(content)) {
            var embedBuilder = new EmbedBuilder();
            StringBuilder descriptionBuilder = embedBuilder.getDescriptionBuilder();
            descriptionBuilder.append(content);
            messageChannel.sendMessage(embedBuilder.build()).queue();
        }
    }

    public void setMessageChannel(MessageChannel messageChannel) {
        this.messageChannel = messageChannel;
    }
}
