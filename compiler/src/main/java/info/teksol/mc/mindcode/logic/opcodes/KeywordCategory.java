package info.teksol.mc.mindcode.logic.opcodes;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/// Defines keyword categories. The primary purpose of keyword categories is the ability to declare additional keywords
/// using the `#declare` directive.
@NullMarked
public enum KeywordCategory {
    linkedBlock,
    builtin,

    alignment,
    blockGroup,
    lookupType,
    markerType,
    radarSort,
    radarTarget,
    settableTileLayer,
    statusEffect,
    tileLayer,
    ;

    private static final Map<String, KeywordCategory> MAP = Stream.of(values())
            .collect(Collectors.toMap(Enum::name, o -> o));

    public static @Nullable KeywordCategory byName(String categoryName) {
        return MAP.get(categoryName);
    }
}
