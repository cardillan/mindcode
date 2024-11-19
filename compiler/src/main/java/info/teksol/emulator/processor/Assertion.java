package info.teksol.emulator.processor;

import info.teksol.mindcode.ToolMessage;

import java.util.Objects;

public record Assertion(String expected, String actual, String title) {

    public boolean success() {
        return Objects.equals(expected, actual);
    }

    public boolean failure() {
        return !Objects.equals(expected, actual);
    }

    public ToolMessage createMessage() {
        return ToolMessage.info("""
                        Assertion "%s" (%s):
                            Expected :%s
                            Actual   :%s""",
                title, Objects.equals(expected, actual) ? "success" : "failure", expected, actual);
    }

    public String generateErrorMessage() {
        return "Failed test " + title + ": expected " + expected + ", actual " + actual + ".";
    }
}
