package io.github.cardillan.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record Weather(
        String contentName,
        String name,
        int id,
        int logicId
) implements MindustryContent {

    @Override
    public ContentType contentType() {
        return ContentType.WEATHER;
    }

}
