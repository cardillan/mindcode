package info.teksol.mindcode.logic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum ProcessorVersion {
    V6,
    V7,
    V7A,
    V8A,
    MAX,
    ;

    public boolean matches(ProcessorVersion min, ProcessorVersion max) {
        return ordinal() >= min.ordinal() && ordinal() <= max.ordinal();
    }

    public static Set<ProcessorVersion> matching(ProcessorVersion min, ProcessorVersion max) {
        return Set.copyOf(ALL.stream().filter(v -> v.matches(min, max)).toList());
    }

    private static final List<ProcessorVersion> ALL = List.of(values());
    private static final Map<String, ProcessorVersion> MAP = ALL.stream()
            .filter(v -> v != MAX)
            .collect(Collectors.toMap(v -> v.name().substring(1), v -> v));

    public static ProcessorVersion byCode(String code) {
        return MAP.get(code);
    }
}
