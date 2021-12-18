package be.thibaulthelsmoortel.currencyconverterbot.commands;

import be.thibaulthelsmoortel.currencyconverterbot.commands.core.BotCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

/**
 * Basic command providing an url where issues can be reported.
 *
 * @author Thibault Helsmoortel
 */
@Command(name = "issue", description = "Provides an url where issues can be reported.")
@Component
public class IssuesCommand extends BotCommand<String> {

    @Value("${bot.issues.url}")
    private String issuesUrl;

    @Override
    public String call() {
        String message = null;
        if (getEvent() instanceof MessageReceivedEvent messageReceivedEvent) {
            message = issuesUrl;

            messageReceivedEvent.getChannel().sendMessage(message).queue();
        }

        return message;
    }
}
