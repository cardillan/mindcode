package io.github.cardillan.mindcode.emulator;

import io.github.cardillan.mindcode.logic.arguments.AssertionDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record TypeAssertion(AssertionDataType expectedType, LVar actualValue, String title) implements EqualityAssertion {

    @Override
    public String expected() {
        return expectedType.toString();
    }

    @Override
    public String actual() {
        return AssertionDataType.actualType(actualValue);
    }

    @Override
    public boolean success() {
        return expectedType.matches(actualValue);
    }
}
