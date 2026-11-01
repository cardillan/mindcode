package io.github.cardillan.mc.emulator;

import io.github.cardillan.mc.messages.MessageLevel;
import io.github.cardillan.mc.messages.MindcodeMessage;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EqualityAssertion extends Assertion {
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
