package info.teksol.mc.profile.options;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class BooleanCompilerOptionValue extends CompilerOptionValue<Boolean> {
    private final String trueValue;
    private final String falseValue;

    public BooleanCompilerOptionValue(Enum<?> option, String optionName, String flag, String description, OptionMultiplicity multiplicity,
            SemanticStability stability, OptionScope scope, OptionAvailability availability, OptionCategory category,
            String trueValue, String falseValue, boolean defaultValue) {

        super(option, optionName, flag, description, Boolean.class, multiplicity, stability, scope, availability, category, defaultValue);
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    public BooleanCompilerOptionValue(Enum<?> option, String flag, String description, OptionMultiplicity multiplicity,
            SemanticStability stability, OptionScope scope, OptionAvailability availability, OptionCategory category,
            String trueValue, String falseValue, boolean defaultValue) {

        super(option, flag, description, Boolean.class, multiplicity, stability, scope, availability, category, defaultValue);
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    public BooleanCompilerOptionValue(Enum<?> option, String flag, String description, OptionMultiplicity multiplicity,
            SemanticStability stability, OptionScope scope, OptionAvailability availability, OptionCategory category,
            boolean defaultValue) {
        this(option, flag, description, multiplicity, stability, scope, availability, category,
                "true", "false", defaultValue);
    }

    public BooleanCompilerOptionValue(Enum<?> option, String optionName, String flag, String description, OptionMultiplicity multiplicity,
            SemanticStability stability, OptionScope scope, OptionAvailability availability, OptionCategory category,
            boolean defaultValue) {
        this(option, optionName, flag, description, multiplicity, stability, scope, availability, category,
                "true", "false", defaultValue);
    }

    public String getTrueValue() {
        return trueValue;
    }

    public String getFalseValue() {
        return falseValue;
    }

    @Override
    public @Nullable Boolean convert(String value) {
        if (trueValue.equalsIgnoreCase(value)) {
            return true;
        } else if (falseValue.equalsIgnoreCase(value)) {
            return false;
        } else {
            return null;
        }
    }

    @Override
    public List<String> acceptedValues() {
        return List.of(trueValue, falseValue);
    }
}
