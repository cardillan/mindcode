package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record Unit(
        String contentName,
        String name,
        int id
) implements MindustryContent {

    @Override
    public ContentType contentType() {
        return ContentType.UNIT;
    }

    public static int count() {
        return MindustryContents.UNIT_MAP.size();
    }

    public static @Nullable Unit forId(int id) {
        return MindustryContents.UNIT_ID_MAP.get(id);
    }

    public static @Nullable Unit forName(String name) {
        return MindustryContents.UNIT_MAP.get(name);
    }
}
