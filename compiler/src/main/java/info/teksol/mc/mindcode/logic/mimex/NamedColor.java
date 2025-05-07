package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record NamedColor(String name, double color) implements NamedContent {

    @Override
    public String contentName() {
        return name;
    }
}
