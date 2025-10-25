package info.teksol.mc.profile;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

@NullMarked
public enum RuntimeErrorReporting {
    NONE        (0),
    ASSERT      (1),
    MINIMAL     (2),
    SIMPLE      (3),
    DESCRIBED   (4),
    ;

    private final int size;

    RuntimeErrorReporting(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public int getExecutionSteps() {
        return Math.min(size, 2);
    }

    private static final Map<String, RuntimeErrorReporting> VALUE_MAP = createValueMap();

    private static Map<String, RuntimeErrorReporting> createValueMap() {
        return EnumUtils.createValueMap(values());
    }

    public static @Nullable RuntimeErrorReporting byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static RuntimeErrorReporting byName(String level, RuntimeErrorReporting defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }

    public static Collection<String> allowedValues() {
        return VALUE_MAP.keySet();
    }
}
