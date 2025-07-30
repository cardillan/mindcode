package info.teksol.mc.profile.options;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum OptionAvailability {
    UNIVERSAL,
    COMMAND_LINE,
    DIRECTIVE,
    NONE,
    ;

    public boolean isCommandline() {
        return this == COMMAND_LINE || this == UNIVERSAL;
    }

    public boolean isDirective() {
        return this == DIRECTIVE || this == UNIVERSAL;
    }
}
