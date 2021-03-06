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

package be.thibaulthelsmoortel.currencyconverterbot.commands.core;

import java.util.concurrent.Callable;
import net.dv8tion.jda.api.events.Event;
import picocli.CommandLine.Command;

/**
 * Command definition providing execution context.
 *
 * @author Thibault Helsmoortel
 */
@Command(mixinStandardHelpOptions = true, versionProvider = VersionProvider.class)
public abstract class BotCommand implements Callable {

    private Event event;

    protected Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
