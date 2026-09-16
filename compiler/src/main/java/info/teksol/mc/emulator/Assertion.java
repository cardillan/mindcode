package info.teksol.mc.emulator;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.messages.MindcodeMessage;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Assertion {
    boolean success();

    default boolean failure() {
        return !success();
    }

    String title();

    String expected();

    String actual();

    default MindcodeMessage createMessage() {
        return new EmulatorMessage(success() ? MessageLevel.INFO : MessageLevel.ERROR,
                null, -1, null,
                String.format("""
                                %s "%s" (%s):
                                    Expected : %s
                                    Actual   : %s""",
                        getClass().getSimpleName(), title(),
                        success() ? "success" : "failure", expected(), actual()));
    }

    default String generateErrorMessage() {
        return "Failed test " + title() + ": expected " + expected() + ", actual " + actual() + ".";
    }
}
