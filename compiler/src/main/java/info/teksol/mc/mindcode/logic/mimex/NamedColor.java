package info.teksol.mc.mindcode.logic.mimex;

public record NamedColor(String name, double color) implements NamedContent {

    @Override
    public String contentName() {
        return name;
    }
}
