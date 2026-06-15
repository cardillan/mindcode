package info.teksol.mc.messages;

import org.jspecify.annotations.NullMarked;

import java.util.function.Predicate;

@NullMarked
public abstract class AbstractMessageEmitter implements MessageEmitter {
    protected final MessageConsumer messageConsumer;
    private Predicate<MindcodeMessage> filter = message -> true;

    public AbstractMessageEmitter(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public MessageConsumer messageConsumer() {
        return messageConsumer;
    }

    @Override
    public void addMessage(MindcodeMessage message) {
        if (filter.test(message)) {
            messageConsumer.addMessage(message);
        }
    }

    public void runWithMessageFilter(Predicate<MindcodeMessage> filter, Runnable runnable) {
        Predicate<MindcodeMessage> previous = this.filter;
        try {
            this.filter = filter;
            runnable.run();
        } finally {
            this.filter = previous;
        }
    }
}
