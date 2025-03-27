package info.teksol.mc.profile;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public enum MemoryModel {
    VOLATILE,
    ALIASED,
    RESTRICTED,
    ;

    private static final Map<String, MemoryModel> VALUE_MAP = createValueMap();

    private static Map<String, MemoryModel> createValueMap() {
        return EnumUtils.createValueMap(values());
    }

    public static @Nullable MemoryModel byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static MemoryModel byName(String level, MemoryModel defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }
}
