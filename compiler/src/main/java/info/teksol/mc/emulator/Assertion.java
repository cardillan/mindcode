package info.teksol.mc.emulator;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.messages.MindcodeMessage;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record Assertion(String expected, String actual, String title) {

    public boolean success() {
        return Objects.equals(expected, actual);
    }

    public boolean failure() {
        return !Objects.equals(expected, actual);
    }

    public MindcodeMessage createMessage() {
        return new EmulatorMessage(success() ? MessageLevel.INFO : MessageLevel.ERROR,
                null, -1, null,
                String.format("""
                                Assertion "%s" (%s):
                                    Expected :%s
                                    Actual   :%s""",
                        title, Objects.equals(expected, actual) ? "success" : "failure", expected, actual));
    }

    public String generateErrorMessage() {
        return "Failed test " + title + ": expected " + expected + ", actual " + actual + ".";
    }
}
