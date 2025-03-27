package info.teksol.mc.profile;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public enum GenerationGoal {
    SIZE,
    SPEED,
    AUTO,
    ;

    private static final Map<String, GenerationGoal> VALUE_MAP = createValueMap();

    private static Map<String, GenerationGoal> createValueMap() {
        return EnumUtils.createValueMap(values());
    }

    public static @Nullable GenerationGoal byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static GenerationGoal byName(String level, GenerationGoal defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }
}
