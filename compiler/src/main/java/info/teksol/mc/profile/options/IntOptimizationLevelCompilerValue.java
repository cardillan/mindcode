package info.teksol.mc.profile.options;

import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class IntOptimizationLevelCompilerValue extends IntegerCompilerOptionValue {
    private final List<CompilerOptionValue<OptimizationLevel>> delegates;

    public IntOptimizationLevelCompilerValue(Enum<?> option, String optionName, String flag, String description,
            OptionMultiplicity multiplicity, SemanticStability stability, OptionScope scope, OptionAvailability availability,
            OptionCategory category, List<CompilerOptionValue<OptimizationLevel>> delegates) {
        super(option, optionName, flag, description, multiplicity, stability, scope, availability, category,
                0, OptimizationLevel.allowedValues().size(), OptimizationLevel.allowedValues().size());
        this.delegates = delegates;
    }

    @Override
    public void setValues(List<Integer> values) {
        List<OptimizationLevel> list = values.stream().map(OptimizationLevel::byIndex).toList();
        delegates.forEach(delegate -> delegate.setValues(list));
    }

    @Override
    public int encodeSize() {
        return 1;
    }

    @Override
    public int encode() {
        return 0;
    }

    @Override
    public void decode(int encoded) {
        // Do nothing
    }
}
