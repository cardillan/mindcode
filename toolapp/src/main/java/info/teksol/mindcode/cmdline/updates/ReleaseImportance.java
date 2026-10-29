package info.teksol.mindcode.cmdline.updates;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

@NullMarked
public enum ReleaseImportance {
    OPTIONAL,
    RECOMMENDED,
    CRITICAL;

    @JsonCreator
    public static @Nullable ReleaseImportance fromJson(@Nullable String value) {
        return value == null ? null : valueOf(value.toUpperCase(Locale.ROOT));
    }
}
