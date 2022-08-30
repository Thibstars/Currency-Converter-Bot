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

import java.util.Set;
import java.util.concurrent.Callable;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

/**
 * Command definition providing execution context.
 *
 * @author Thibault Helsmoortel
 */
@Command(mixinStandardHelpOptions = true, versionProvider = VersionProvider.class)
public abstract class BotCommand<T> implements Callable<T> {

    @SuppressWarnings("UnusedDeclaration") // Injected by Picocli
    @Spec
    private CommandLine.Model.CommandSpec spec;

    private Event event;

    protected Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void validate() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
            Set<ConstraintViolation<BotCommand<T>>> violations = validator.validate(this);

            if (!violations.isEmpty()) {
                StringBuilder errorMsg = new StringBuilder();
                for (ConstraintViolation<BotCommand<T>> violation : violations) {
                    errorMsg.append("Error: ").append(violation.getMessage()).append("\n");
                }
                throw new ParameterException(spec.commandLine(), errorMsg.toString());
            }
        }
    }

    public abstract SlashCommandData getSlashCommandData();
}
