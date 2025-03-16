package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record Liquid(
        String contentName,
        String name,
        int id
) implements MindustryContent {

    @Override
    public ContentType contentType() {
        return ContentType.LIQUID;
    }

    public static int count() {
        return MindustryContents.logicCount(MindustryContents.LIQUID_MAP);
    }

    public static @Nullable Liquid forId(int id) {
        return MindustryContents.LIQUID_ID_MAP.get(id);
    }

    public static @Nullable Liquid forName(String name) {
        return MindustryContents.LIQUID_MAP.get(name);
    }
}
