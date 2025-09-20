package info.teksol.mc.profile.options;

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
