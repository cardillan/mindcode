package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record LAccess(
        String contentName,
        String name,
        boolean sensor,
        boolean control,
        boolean setprop,
        String parameters
) implements MindustryContent {

    @Override
    public ContentType contentType() {
        return ContentType.LACCESS;
    }
}
