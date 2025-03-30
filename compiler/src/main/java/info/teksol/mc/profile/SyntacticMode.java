package info.teksol.mc.profile;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

@NullMarked
public enum SyntacticMode {
    STRICT,
    MIXED,
    RELAXED,
    ;

    private static final Map<String, SyntacticMode> VALUE_MAP = createValueMap();

    private static Map<String, SyntacticMode> createValueMap() {
        return EnumUtils.createValueMap(values());
    }

    public static @Nullable SyntacticMode byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static SyntacticMode byName(String level, SyntacticMode defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }

    public static Collection<String> allowedValues() {
        return VALUE_MAP.keySet();
    }
}
