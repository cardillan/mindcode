package io.github.cardillan.mindcode.emulator;

import io.github.cardillan.mindcode.messages.MindcodeMessage;
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
