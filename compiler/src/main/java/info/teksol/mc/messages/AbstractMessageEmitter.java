package info.teksol.mc.messages;

import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractMessageEmitter implements MessageEmitter {
    protected final MessageConsumer messageConsumer;

    public AbstractMessageEmitter(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public MessageConsumer messageConsumer() {
        return messageConsumer;
    }

    @Override
    public void addMessage(MindcodeMessage message) {
        messageConsumer.addMessage(message);
    }
}
