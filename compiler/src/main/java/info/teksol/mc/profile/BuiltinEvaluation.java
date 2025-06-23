package info.teksol.mc.profile;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public enum BuiltinEvaluation {
    NONE,
    COMPATIBLE,
    FULL,
    ;

    private static final Map<String, BuiltinEvaluation> VALUE_MAP = createValueMap();

    private static Map<String, BuiltinEvaluation> createValueMap() {
        return EnumUtils.createValueMap(values());
    }

    public static @Nullable BuiltinEvaluation byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static BuiltinEvaluation byName(String level, BuiltinEvaluation defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }

    public static Collection<String> allowedValues() {
        return VALUE_MAP.keySet();
    }
}
