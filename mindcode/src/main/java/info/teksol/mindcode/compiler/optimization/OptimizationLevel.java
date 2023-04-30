package info.teksol.mindcode.compiler.optimization;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum OptimizationLevel {
    OFF,
    BASIC,
    AGGRESSIVE,
    ;

    public static final Map<String, OptimizationLevel> VALUE_MAP =
            Stream.of(values()).collect(Collectors.toMap(e -> e.name().toLowerCase(), e -> e));
}
