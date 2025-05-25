package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public enum ContentType {
    UNKNOWN,
    BLOCK(true),
    ITEM(true),
    LIQUID(true),
    STATUS,
    TEAM(true),
    UNIT(true),
    UNIT_COMMAND,
    LACCESS,
    LVAR,
    WEATHER,
    ;

    public final boolean hasLookup;

    ContentType(boolean hasLookup) {
        this.hasLookup = hasLookup;
    }

    ContentType() {
        this.hasLookup = false;
    }

    public @Nullable String getLookupKeyword() {
        return hasLookup ? name().toLowerCase() : null;
    }

    private static final Map<String, ContentType> VALUE_MAP = createValueMap();

    private static Map<String, ContentType> createValueMap() {
        return Stream.of(ContentType.values())
                .collect(Collectors.toMap(Enum::name, e -> e));
    }

    public static @Nullable ContentType byName(String contentType) {
        return VALUE_MAP.get(contentType);
    }

    public static ContentType byName(String contentType, ContentType defaultValue) {
        return VALUE_MAP.getOrDefault(contentType, defaultValue);
    }

}
