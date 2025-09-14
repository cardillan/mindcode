package info.teksol.mc.mindcode.logic.opcodes;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@NullMarked
public enum ProcessorVersion {
    V6      (0, 6, 0, "v126.2"),
    V7      (0, 7, 0, "v146"),
    V7A     (1, 7, 1, "v146"),
    V8A     (1,8, 0, "v149"),
    V8B     (1, 8, 1, "be"),
    MAX     (1, 8, 0, "be"),
    ;

    // A change in the generation number reflects differences in mappings from Mindcode to mlog.
    // Two targets with different generations are always incompatible.
    private final int generation;
    public final int major;
    public final int minor;
    public final String mimexVersion;

    ProcessorVersion(int generation, int major, int minor, String mimexVersion) {
        this.generation = generation;
        this.major = major;
        this.minor = minor;
        this.mimexVersion = mimexVersion;
    }

    public boolean atLeast(ProcessorVersion min) {
        return ordinal() >= min.ordinal();
    }

    public boolean atMost(ProcessorVersion max) {
        return ordinal() <= max.ordinal();
    }

    public boolean isCompatibleWith(ProcessorVersion other) {
        return generation == other.generation && ordinal() <= other.ordinal();
    }

    public boolean matches(ProcessorVersion min, ProcessorVersion max) {
        return ordinal() >= min.ordinal() && ordinal() <= max.ordinal();
    }

    public static Set<ProcessorVersion> matching(ProcessorVersion min, ProcessorVersion max) {
        return Set.copyOf(ALL.stream().filter(v -> v.matches(min, max)).toList());
    }

    public String versionName() {
        return major + "." + minor;
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
