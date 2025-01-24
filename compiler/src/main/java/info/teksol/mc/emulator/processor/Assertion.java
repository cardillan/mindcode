package info.teksol.mc.emulator.processor;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.messages.ToolMessage;
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

    public ToolMessage createMessage() {
        return new ToolMessage(success() ? MessageLevel.INFO : MessageLevel.ERROR,
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
