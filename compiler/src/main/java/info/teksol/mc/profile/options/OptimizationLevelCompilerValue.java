package info.teksol.mc.profile.options;

import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class OptimizationLevelCompilerValue extends EnumCompilerOptionValue<OptimizationLevel> {
    private final List<CompilerOptionValue<OptimizationLevel>> delegates;

    public OptimizationLevelCompilerValue(Enum<?> option, String flag, String description, OptionMultiplicity multiplicity,
            SemanticStability stability, OptionScope scope, OptionAvailability availability,
            OptionCategory category, List<CompilerOptionValue<OptimizationLevel>> delegates) {
        super(option, flag, description, OptimizationLevel.class, multiplicity, stability, scope, availability, category,
                OptimizationLevel.NONE);

        this.delegates = delegates;
    }

    @Override
    public void setValues(List<OptimizationLevel> values) {
        delegates.forEach(delegate -> delegate.setValues(values));
    }
}
