package info.teksol.mc.profile;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public enum FileReferences {
    PATH,
    URI,
    WINDOWS_URI,
    ;

    private static final Map<String, FileReferences> VALUE_MAP = createValueMap();

    private static Map<String, FileReferences> createValueMap() {
        return EnumUtils.createValueMap(values());
    }

    public static @Nullable FileReferences byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static FileReferences byName(String level, FileReferences defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }
}
