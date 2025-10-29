package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public enum ContentType {
    UNKNOWN,
    ITEM(0, true),
    BLOCK(1, true),
    LIQUID(4, true),
    STATUS (5),
    UNIT(6, true),
    WEATHER(7),
    TEAM(15, true),
    UNIT_COMMAND(16),
    UNIT_STANCE(17),
    LACCESS,
    LVAR,
    ;

    public final int id;
    public final boolean hasLookup;

    ContentType(int id, boolean hasLookup) {
        this.id = id;
        this.hasLookup = hasLookup;
    }

    ContentType(int id) {
        this.id = id;
        this.hasLookup = false;
    }

    ContentType() {
        this.id = -1;
        this.hasLookup = false;
    }

    public String getLookupKeyword() {
        if (!hasLookup) {
            throw new IllegalStateException("Lookup not supported");
        }
        return name().toLowerCase();
    }

    private static final Map<String, ContentType> VALUE_MAP = createValueMap();
    private static final Map<Integer, ContentType> ID_MAP = createIdMap();

    private static Map<String, ContentType> createValueMap() {
        return Stream.of(ContentType.values())
                .collect(Collectors.toMap(Enum::name, e -> e));
    }

    private static Map<Integer, ContentType> createIdMap() {
        return Stream.of(ContentType.values())
                .filter(e -> e.id >= 0)
                .collect(Collectors.toMap(e -> e.id, e -> e));
    }

    public static @Nullable ContentType byName(String contentType) {
        return VALUE_MAP.get(contentType);
    }

    public static @Nullable ContentType byId(int id) {
        return ID_MAP.get(id);
    }

    public static ContentType byName(String contentType, ContentType defaultValue) {
        return VALUE_MAP.getOrDefault(contentType, defaultValue);
    }

}
