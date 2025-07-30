package info.teksol.mc.profile.options;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class StringCompilerOptionValue extends CompilerOptionValue<String> {
    private final String acceptedValuesDescription;

    public StringCompilerOptionValue(Enum<?> option, String optionName, String flag, String description,
            String acceptedValuesDescription, OptionMultiplicity multiplicity, SemanticStability stability, OptionScope scope,
            OptionAvailability availability, OptionCategory category, List<String> defaultValue) {
        super(option, optionName, flag, description, String.class, multiplicity, stability, scope, availability, category, defaultValue);
        this.acceptedValuesDescription = acceptedValuesDescription;
    }

    public StringCompilerOptionValue(Enum<?> option, String flag, String description, String acceptedValuesDescription,
            OptionMultiplicity multiplicity, SemanticStability stability, OptionScope scope, OptionAvailability availability,
            OptionCategory category, List<String> defaultValue) {
        this(option, EnumUtils.toKebabCase(option), flag, description, acceptedValuesDescription,
                multiplicity, stability, scope, availability, category, defaultValue);
    }

    @Override
    public @Nullable String convert(String value) {
        return value;
    }

    @Override
    public String acceptedValuesDescription() {
        return acceptedValuesDescription;
    }
}
