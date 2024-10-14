package info.teksol.mindcode.compiler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum SortCategory {
    LINKED,
    PARAMS,
    GLOBALS,
    MAIN,
    LOCALS,
    ALL,
    NONE,
    ;

    public static String usefulCategories() {
        return Arrays.stream(values())
                .filter(c -> c != ALL && c != NONE)
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }

    private static final Map<String, SortCategory> VALUE_MAP = createValueMap();

    private static final List<SortCategory> ALL_CATEGORIES = List.of(values());

    private static Map<String, SortCategory> createValueMap() {
        return Arrays.stream(values()).collect(Collectors.toMap(v -> v.name().toLowerCase(), v -> v));
    }

    public static SortCategory byName(String category) {
        return VALUE_MAP.get(category.toLowerCase());
    }

    public static List<SortCategory> getAllCategories() {
        return ALL_CATEGORIES;
    }
}
