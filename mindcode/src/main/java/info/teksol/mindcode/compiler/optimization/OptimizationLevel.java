package info.teksol.mindcode.compiler.optimization;

import java.util.HashMap;
import java.util.Map;

public enum OptimizationLevel {
    NONE,
    BASIC,
    ADVANCED,
    EXPERIMENTAL,
    ;

    private static final Map<String, OptimizationLevel> VALUE_MAP = createValueMap();

    private static Map<String, OptimizationLevel> createValueMap() {
        Map<String, OptimizationLevel> map = new HashMap<>();
        for (OptimizationLevel level : values()) {
            map.put(level.name().toLowerCase(), level);
        }
        map.put("off", OptimizationLevel.NONE);
        map.put("aggressive", OptimizationLevel.ADVANCED);
        return Map.copyOf(map);
    }

    public static OptimizationLevel byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static OptimizationLevel byName(String level, OptimizationLevel defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }
}
