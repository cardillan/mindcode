package info.teksol.mc.profile.options;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum OptionMultiplicity {
    ZERO            (""),
    ZERO_OR_ONCE    ("?"),
    ZERO_OR_MORE    ("*"),
    ONCE            (""),
    ONCE_OR_MORE    ("+"),
    ;

    public final String nargs;

    OptionMultiplicity(String nargs) {
        this.nargs = nargs;
    }

    public boolean matchesMultiple() {
        return this == ZERO_OR_MORE || this == ONCE_OR_MORE;
    }

    public boolean matchesNone() {
        return this == ZERO || this == ZERO_OR_ONCE || this == ZERO_OR_MORE;
    }
}
