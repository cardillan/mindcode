package info.teksol.mc.profile;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Remarks {
    NONE,
    PASSIVE,
    ACTIVE,
    ;

    private static final Map<String, Remarks> VALUE_MAP = createValueMap();

    private static Map<String, Remarks> createValueMap() {
        return Stream.of(Remarks.values())
                .collect(Collectors.toMap(e -> e.name().toLowerCase(),
                        e -> e));
    }

    public static Remarks byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static Remarks byName(String level, Remarks defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }
}
