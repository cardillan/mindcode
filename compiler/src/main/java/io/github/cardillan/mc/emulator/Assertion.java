package io.github.cardillan.mc.emulator;

import io.github.cardillan.mc.messages.MindcodeMessage;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Assertion {
    boolean success();
    default boolean failure() {
        return !success();
    }

    String title();

    MindcodeMessage createMessage();
    String generateErrorMessage();
}
