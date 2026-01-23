package info.teksol.mc.messages;

import info.teksol.mc.util.StringUtils;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum MessageLevel {
    ERROR,
    WARNING,
    INFO,
    DEBUG;

    private final String title;

    public boolean strongerOrEqual(MessageLevel other) {
        return ordinal() <= other.ordinal();
    }

    public boolean weakerOrEqual(MessageLevel other) {
        return ordinal() >= other.ordinal();
    }

    MessageLevel() {
        title = StringUtils.titleCase(name());
    }

    public String getTitle() {
        return title;
    }
}
