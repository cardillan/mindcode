package info.teksol.mc.profile;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public enum FinalCodeOutput {
    PLAIN,
    FLAT_AST,
    DEEP_AST,
    SOURCE,
    ;

    private static final Map<String, FinalCodeOutput> VALUE_MAP = createValueMap();

    private static Map<String, FinalCodeOutput> createValueMap() {
        return Stream.of(FinalCodeOutput.values())
                .collect(Collectors.toMap(e -> e.name().toLowerCase(),
                        e -> e));
    }

    public static @Nullable FinalCodeOutput byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static FinalCodeOutput byName(String level, FinalCodeOutput defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }
}
