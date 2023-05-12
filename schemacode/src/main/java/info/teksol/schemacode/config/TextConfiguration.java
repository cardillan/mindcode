package info.teksol.schemacode.config;

public record TextConfiguration(String value) implements Configuration {

    public static final TextConfiguration EMPTY = new TextConfiguration("");

    @Override
    public String toString() {
        return '"' + value + '"';
    }
}
