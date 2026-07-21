package info.teksol.mc.messages;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class MessageLogger implements MessageConsumer {
    private final MessageConsumer delegate;
    private int errorCount;
    private int warningCount;

    public MessageLogger(MessageConsumer delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized void addMessage(MindcodeMessage message) {
        // Track errors and warnings on the fly so that the numbers are accurate even when aborted
        if (message.isError()) errorCount++;
        if (message.isWarning()) warningCount++;
        delegate.accept(message);
    }

    public boolean hasErrors() {
        return errorCount > 0;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }
}
