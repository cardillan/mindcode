package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record Item(
        String contentName,
        String name,
        int id
) implements MindustryContent {

    @Override
    public ContentType contentType() {
        return ContentType.ITEM;
    }

    public static int count() {
        return MindustryContents.logicCount(MindustryContents.ITEM_MAP);
    }

    public static @Nullable Item forId(int id) {
        return MindustryContents.ITEM_ID_MAP.get(id);
    }

    public static @Nullable Item forName(String name) {
        return MindustryContents.ITEM_MAP.get(name);
    }
}
