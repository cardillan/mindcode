package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ContentType {
    UNKNOWN,
    BLOCK,
    ITEM,
    LIQUID,
    STATUS,
    TEAM,
    UNIT,
    UNIT_COMMAND,
    LACCESS,
    LVAR,
    ;

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
//