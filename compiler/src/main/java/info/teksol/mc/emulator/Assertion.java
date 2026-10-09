package info.teksol.mc.emulator;

import info.teksol.mc.messages.MindcodeMessage;
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
