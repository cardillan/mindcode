package io.github.cardillan.mindcode.profile.options;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum OptionScope {
    GLOBAL,
    MODULE,
    LOCAL,
    ;

    public boolean isIncludedIn(OptionScope other) {
        return ordinal() >= other.ordinal();
    }
}
