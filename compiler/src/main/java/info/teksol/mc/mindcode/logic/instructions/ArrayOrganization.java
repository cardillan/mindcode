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
        // Tries re-inlining short arrays too
        return this == INTERNAL || this == SHORT;
    }

    public ArrayOrganization inline() {
        return canInline() ? INLINED : this;
    }

    public boolean canInlineShort() {
        // Tries re-inlining short arrays too
        return this == INTERNAL || this == INLINED;
    }

    public ArrayOrganization inlineShort() {
        return canInlineShort() ? SHORT : this;
    }

    public boolean supportsLookup() {
        return switch(this) {
            case INTERNAL, INLINED, SHORT, LOOKUP -> true;
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
