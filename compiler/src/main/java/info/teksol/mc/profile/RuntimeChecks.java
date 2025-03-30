package info.teksol.mc.profile;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public enum RuntimeChecks {
    NONE        (0),
    ASSERT      (1),
    MINIMAL     (2),
    SIMPLE      (3),
    DESCRIBED   (4),
    ;

    private final int size;

    RuntimeChecks(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    private static final Map<String, RuntimeChecks> VALUE_MAP = createValueMap();

    private static Map<String, RuntimeChecks> createValueMap() {
        return EnumUtils.createValueMap(values());
    }

    public static @Nullable RuntimeChecks byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static RuntimeChecks byName(String level, RuntimeChecks defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }

    public static Collection<String> allowedValues() {
        return VALUE_MAP.keySet();
    }
}
