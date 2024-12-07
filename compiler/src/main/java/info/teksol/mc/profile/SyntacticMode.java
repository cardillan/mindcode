package info.teksol.mc.profile;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SyntacticMode {
    STRICT,
    MIXED,
    RELAXED,
    ;

    private static final Map<String, SyntacticMode> VALUE_MAP = createValueMap();

    private static Map<String, SyntacticMode> createValueMap() {
        return Stream.of(SyntacticMode.values())
                .collect(Collectors.toMap(e -> e.name().toLowerCase(),
                        e -> e));
    }

    public static SyntacticMode byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static SyntacticMode byName(String level, SyntacticMode defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }
}
