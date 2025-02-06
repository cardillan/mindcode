package info.teksol.mc.profile;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return Stream.of(RuntimeChecks.values())
                .collect(Collectors.toMap(e -> e.name().toLowerCase(),
                        e -> e));
    }

    public static @Nullable RuntimeChecks byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static RuntimeChecks byName(String level, RuntimeChecks defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }
}
