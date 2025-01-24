package info.teksol.mc.profile;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public enum GenerationGoal {
    SIZE,
    SPEED,
    AUTO,
    ;

    private static final Map<String, GenerationGoal> VALUE_MAP = createValueMap();

    private static Map<String, GenerationGoal> createValueMap() {
        return Stream.of(GenerationGoal.values())
                .collect(Collectors.toMap(e -> e.name().toLowerCase(),
                        e -> e));
    }

    public static @Nullable GenerationGoal byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static GenerationGoal byName(String level, GenerationGoal defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }
}
