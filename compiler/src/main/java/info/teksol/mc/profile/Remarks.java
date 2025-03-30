package info.teksol.mc.profile;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

@NullMarked
public enum Remarks {
    NONE,
    COMMENTS,
    PASSIVE,
    ACTIVE,
    ;

    private static final Map<String, Remarks> VALUE_MAP = createValueMap();

    private static Map<String, Remarks> createValueMap() {
        return EnumUtils.createValueMap(values());
    }

    public static @Nullable Remarks byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static Remarks byName(String level, Remarks defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }

    public static Collection<String> allowedValues() {
        return VALUE_MAP.keySet();
    }
}
