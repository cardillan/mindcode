package info.teksol.mc.profile.options;

import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class OptimizationOptionValue extends EnumCompilerOptionValue<OptimizationLevel> {

    public OptimizationOptionValue(Enum<?> option, String optionName, String flag, String description, Class<OptimizationLevel> valueType,
            OptionMultiplicity multiplicity, SemanticStability stability, OptionScope scope, OptionAvailability availability,
            OptionCategory category, OptimizationLevel defaultValue) {
        super(option, optionName, flag, description, valueType, multiplicity, stability, scope, availability, category, defaultValue);
    }

    @Override
    public boolean setValues(List<OptimizationLevel> values, OptionScope scope) {
        super.setValues(values, scope);
        return scope != OptionScope.LOCAL || getValue() != OptimizationLevel.NONE;
    }
}
