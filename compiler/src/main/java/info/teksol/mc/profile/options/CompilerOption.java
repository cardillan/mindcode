package info.teksol.mc.profile.options;

public interface CompilerOption {
    Enum<?> getOption();

    String getOptionName();

    String getFlag();

    String[] getNameOrFlag();

    String getDescription();

    OptionAvailability getAvailability();

    SemanticStability getStability();

    OptionCategory getCategory();

    OptionScope getScope();

    OptionMultiplicity getMultiplicity();

    boolean isDefault();

    // By default, options do not encode themselves
    default int encodeSize() {
        return 1;
    }

    default int encode() {
        return 0;
    }

    default void decode(int encoded) {
        // Do nothing
    }
}
