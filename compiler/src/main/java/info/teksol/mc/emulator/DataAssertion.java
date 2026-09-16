package info.teksol.mc.emulator;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record DataAssertion(String expected, String actual, String title) implements Assertion {
    @Override
    public boolean success() {
        return Objects.equals(expected, actual);
    }
}
