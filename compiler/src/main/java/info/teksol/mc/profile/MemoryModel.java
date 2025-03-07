package info.teksol.mc.profile;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public enum MemoryModel {
    VOLATILE,
    ALIASED,
    RESTRICTED,
    ;

    private static final Map<String, MemoryModel> VALUE_MAP = createValueMap();

    private static Map<String, MemoryModel> createValueMap() {
        return Stream.of(MemoryModel.values())
                .collect(Collectors.toMap(e -> e.name().toLowerCase(),
                        e -> e));
    }

    public static @Nullable MemoryModel byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static MemoryModel byName(String level, MemoryModel defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }
}
