package io.github.cardillan.mindcode.messages;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MessageEmitter {
    MessageConsumer messageConsumer();

    default void addMessage(MindcodeMessage message) {
        messageConsumer().addMessage(message);
    }
}
