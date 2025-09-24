package info.teksol.mc.profile.options;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class DoubleCompilerOptionValue extends CompilerOptionValue<Double> {
    public final double min;
    public final double max;

    public DoubleCompilerOptionValue(Enum<?> option, String optionName, String flag, String description, OptionMultiplicity multiplicity,
            SemanticStability stability, OptionScope scope, OptionAvailability availability, OptionCategory category,
            double min, double max, double defaultValue) {
        super(option, optionName, flag, description, Double.class, multiplicity, stability, scope, availability, category, defaultValue);

        this.min = min;
        this.max = max;
    }

    public DoubleCompilerOptionValue(Enum<?> option, String flag, String description, OptionMultiplicity multiplicity,
            SemanticStability stability, OptionScope scope, OptionAvailability availability, OptionCategory category,
            double min, double max, double defaultValue) {
        this(option, EnumUtils.toKebabCase(option), flag, description, multiplicity, stability, scope,
                availability, category, min, max, defaultValue);
    }

    @Override
    public @Nullable Double convert(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void setValue(Double value) {
        if (value == getDefaultValue()) {
            super.setValue(value);
        } else {
            super.setValue(Math.min(Math.max(value, min), max));
        }
    }

    @Override
    public boolean accepts(Double value) {
        return min <= value && value <= max;
    }

    @Override
    public String acceptedValuesDescription() {
        return "{" + min + ".." + max + "}";
    }
}
