package info.teksol.mc.messages;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class ListMessageLogger extends MessageLogger {
    protected final List<MindcodeMessage> messages = new ArrayList<>();

    public ListMessageLogger() {
        super(s -> {});
    }

    public ListMessageLogger(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

    @Override
    public void addMessage(MindcodeMessage message) {
        messages.add(message);
        super.addMessage(message);
    }

    public List<MindcodeMessage> getMessages() {
        return messages;
    }
}
