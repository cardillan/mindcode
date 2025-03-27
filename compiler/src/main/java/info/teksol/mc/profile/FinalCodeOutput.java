package info.teksol.mc.profile;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public enum FinalCodeOutput {
    PLAIN,
    FLAT_AST,
    DEEP_AST,
    SOURCE,
    ;

    private static final Map<String, FinalCodeOutput> VALUE_MAP = createValueMap();

    private static Map<String, FinalCodeOutput> createValueMap() {
        return EnumUtils.createValueMap(values());
    }

    public static @Nullable FinalCodeOutput byName(String level) {
        return VALUE_MAP.get(level.toLowerCase());
    }

    public static FinalCodeOutput byName(String level, FinalCodeOutput defaultValue) {
        return VALUE_MAP.getOrDefault(level.toLowerCase(), defaultValue);
    }
}
