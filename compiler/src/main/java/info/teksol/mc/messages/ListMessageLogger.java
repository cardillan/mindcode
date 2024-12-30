package info.teksol.mc.messages;

import java.util.ArrayList;
import java.util.List;

public class ListMessageLogger extends AbstractMessageLogger {
    private final MessageConsumer messageConsumer;
    protected final List<MindcodeMessage> messages = new ArrayList<>();
    private boolean hasErrors = false;
    private boolean hasWarnings = false;

    public ListMessageLogger() {
        this.messageConsumer = s -> {};
    }

    public ListMessageLogger(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void addMessage(MindcodeMessage message) {
        switch (message.level()) {
            case ERROR -> hasErrors = true;
            case WARNING -> hasWarnings = true;
        }
        messages.add(message);
        messageConsumer.accept(message);
    }

    public List<MindcodeMessage> getMessages() {
        return messages;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public boolean hasWarnings() {
        return hasWarnings;
    }

    public boolean hasErrorsOrWarnings() {
        return hasErrors || hasWarnings;
    }
}
