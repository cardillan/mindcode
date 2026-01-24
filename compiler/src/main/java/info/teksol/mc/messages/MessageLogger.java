package info.teksol.mc.messages;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class MessageLogger implements MessageConsumer {
    private final MessageConsumer delegate;
    private boolean error = false;

    public MessageLogger(MessageConsumer delegate) {
        this.delegate = delegate;
    }

    @Override
    public void addMessage(MindcodeMessage message) {
        if (message.isError()) error = true;
        delegate.accept(message);
    }

    public boolean hasErrors() {
        return error;
    }
}
