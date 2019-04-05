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

import picocli.CommandLine.IVersionProvider;

/**
 * Provides the implementation version of the current package.
 *
 * @author Thibault Helsmoortel
 */
public class VersionProvider implements IVersionProvider {

    // The version number is populated on packaging phase, so it will be unknown while testing/developing
    private static final String VERSION_UNKNOWN = "unknown";

    private Package pack;

    VersionProvider() {
        this.pack = getClass().getPackage();
    }

    @Override
    public String[] getVersion() {
        String implementationVersion = pack.getImplementationVersion();
        return new String[]{implementationVersion != null ? implementationVersion : VERSION_UNKNOWN};
    }

    // Visible for testing
    void setPack(Package pack) {
        this.pack = pack;
    }
}
