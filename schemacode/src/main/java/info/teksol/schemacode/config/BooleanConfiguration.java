package info.teksol.schemacode.config;

public record BooleanConfiguration(boolean value) implements Configuration {

    public static final BooleanConfiguration FALSE = new BooleanConfiguration(false);

    public static final BooleanConfiguration TRUE = new BooleanConfiguration(true);

    public static BooleanConfiguration of(boolean value) {
        return value ? TRUE : FALSE;
    }
}
