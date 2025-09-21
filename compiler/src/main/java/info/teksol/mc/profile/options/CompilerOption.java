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
}
