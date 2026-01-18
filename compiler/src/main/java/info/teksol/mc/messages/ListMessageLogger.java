package info.teksol.mc.messages;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class ListMessageLogger extends AbstractMessageLogger {
    private final MessageConsumer messageConsumer;
    protected final List<MindcodeMessage> messages = new ArrayList<>();

    public ListMessageLogger() {
        this.messageConsumer = s -> {};
    }

    public ListMessageLogger(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void addMessage(MindcodeMessage message) {
        messages.add(message);
        messageConsumer.accept(message);
    }

    public List<MindcodeMessage> getMessages() {
        return messages;
    }
}
