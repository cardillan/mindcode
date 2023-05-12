package info.teksol.schemacode.config;

public record IntConfiguration(int value) implements Configuration {

    public static final IntConfiguration ZERO = new IntConfiguration(0);

}
