package info.teksol.mc.mindcode.logic.instructions;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum ArrayOrganization {
    NONE,
    INTERNAL,
    INLINED,
    SINGLE,
    SHORT,
    LOOKUP,
    EXTERNAL,
    ;

    private final String name;

    ArrayOrganization() {
        this.name = name().toLowerCase();
    }

    public String getName() {
        return name;
    }

    public boolean canInline() {
        return this == INTERNAL;
    }

    public boolean supportsLookup() {
        return switch(this) {
            case INTERNAL, INLINED, SINGLE, SHORT, LOOKUP -> true;
            default -> false;
        };
    }

    public boolean isInlined() {
        return switch(this) {
            case INLINED, SINGLE, SHORT, LOOKUP -> true;
            default -> false;
        };
    }
}
