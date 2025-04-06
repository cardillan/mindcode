package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record Liquid(
        String contentName,
        String name,
        int id,
        int logicId
) implements MindustryContent {

    @Override
    public ContentType contentType() {
        return ContentType.LIQUID;
    }
}
