package info.teksol.mc.mindcode.logic.opcodes;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@NullMarked
public enum ProcessorVersion {
    V6      (6, 0),
    V7      (7, 0),
    V7A     (7, 1),
    V8A     (8, 0),
    MAX     (8, 0),
    ;

    public final int major;
    public final int minor;

    ProcessorVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public boolean atLeast(ProcessorVersion min) {
        return ordinal() >= min.ordinal();
    }

    public boolean atMost(ProcessorVersion max) {
        return ordinal() <= max.ordinal();
    }

    public boolean matches(ProcessorVersion min, ProcessorVersion max) {
        return ordinal() >= min.ordinal() && ordinal() <= max.ordinal();
    }

    public static Set<ProcessorVersion> matching(ProcessorVersion min, ProcessorVersion max) {
        return Set.copyOf(ALL.stream().filter(v -> v.matches(min, max)).toList());
    }

    private static final List<ProcessorVersion> ALL = List.of(values());
    private static final Map<String, ProcessorVersion> MAP = createMap();

    public static @Nullable ProcessorVersion byCode(String code) {
        return MAP.get(code);
    }

    private static Map<String, ProcessorVersion> createMap() {
        Map<String, ProcessorVersion> result = new LinkedHashMap<>();
        for (ProcessorVersion version : ALL) {
            if (version != MAX) {
                result.put(version.major + "", version);
                result.put(version.major + "." + version.minor, version);
            }
        }
        return result;
    }

    public static List<String> getPossibleVersions() {
        return MAP.entrySet().stream().<String>mapMulti((entry, consumer) -> {
            consumer.accept(entry.getKey());
            if (entry.getValue() != V6) {
                consumer.accept(entry.getKey() + "w");
            }
        }).toList();
    }
}
