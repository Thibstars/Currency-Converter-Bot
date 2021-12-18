package be.thibaulthelsmoortel.currencyconverterbot.commands;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Thibault Helsmoortel
 */
class IssuesCommandTest extends CommandBaseTest  {

    private final IssuesCommand issuesCommand;

    @Value("${bot.issues.url}")
    private String issuesUrl;

    @Mock
    private JDA jda;

    @Autowired
    IssuesCommandTest(IssuesCommand issuesCommand) {
        this.issuesCommand = issuesCommand;
    }

    @BeforeEach
    void setUp() {
        when(messageReceivedEvent.getJDA()).thenReturn(jda);
        when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
        when(messageChannel.sendMessage(anyString())).thenReturn(mock(MessageAction.class));
    }

    @DisplayName("Should return issues url.")
    @Test
    void shouldReturnInviteUrlWithoutPermissions() {
        issuesCommand.setEvent(messageReceivedEvent);

        String message = issuesCommand.call();

        Assertions.assertNotNull(message, "Issues url must not be null.");
        Assertions.assertEquals(issuesUrl, message, "Issues url must be correct.");

        verifyOneMessageSent();
    }

    private void verifyOneMessageSent() {
        verify(messageReceivedEvent).getChannel();
        verify(messageChannel).sendMessage(anyString());
        verifyNoMoreInteractions(messageChannel);
    }

    @DisplayName("Should not process event.")
    @Test
    void shouldNotProcessEvent() throws Exception {
        verifyDoNotProcessEvent(issuesCommand, mock(Event.class));
    }
}