package io.github.cardillan.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record NamedColor(String name, double color) implements NamedContent {

    @Override
    public String contentName() {
        return name;
    }
}
