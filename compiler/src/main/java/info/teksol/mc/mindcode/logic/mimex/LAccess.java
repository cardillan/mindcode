package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record LAccess(
        String contentName,
        String name,
        boolean senseable,
        boolean controllable,
        boolean settable,
        String parameters
) implements MindustryContent {

    @Override
    public ContentType contentType() {
        return ContentType.LACCESS;
    }

    @Override
    public int id() {
        return -1;
    }

    public static @Nullable LAccess forName(String name) {
        return MindustryContents.LACCESS_MAP.get(name);
    }
}
