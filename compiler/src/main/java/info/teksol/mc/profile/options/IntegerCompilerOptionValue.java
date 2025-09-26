package info.teksol.mc.profile.options;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@NullMarked
public class IntegerCompilerOptionValue extends CompilerOptionValue<Integer> {
    public final int min;
    public final int max;

    public IntegerCompilerOptionValue(Enum<?> option, String optionName, String flag, String description, OptionMultiplicity multiplicity,
            SemanticStability stability, OptionScope scope, OptionAvailability availability, OptionCategory category,
            int min, int max, int defaultValue) {
        super(option, optionName, flag, description, Integer.class, multiplicity, stability, scope, availability, category, defaultValue);

        this.min = min;
        this.max = max;
    }

    public IntegerCompilerOptionValue(Enum<?> option, String flag, String description, OptionMultiplicity multiplicity,
            SemanticStability stability, OptionScope scope, OptionAvailability availability, OptionCategory category,
            int min, int max, int defaultValue) {
        this(option, EnumUtils.toKebabCase(option), flag, description, multiplicity, stability, scope,
                availability, category, min, max, defaultValue);
    }

    @Override
    public @Nullable Integer convert(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean accepts(Integer value, Consumer<String> errorReporter) {
        if (value == getDefaultValue() || min <= value && value <= max) {
            return true;
        } else {
            errorReporter.accept(String.format(ERR.DIRECTIVE_VALUE_INTEGER_OUT_OF_RANGE, value, getOptionName(), min, max));
            return false;
        }
    }

    @Override
    public String acceptedValuesDescription() {
        return "{" + min + ".." + max + "}";
    }
}
