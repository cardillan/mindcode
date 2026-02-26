package info.teksol.mc.profile.options;

import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class TargetCompilerOptionValue extends CompilerOptionValue<Target> {

    public TargetCompilerOptionValue(Enum<?> option, String flag, String description, OptionMultiplicity multiplicity,
            SemanticStability stability, OptionScope scope, OptionAvailability availability, OptionCategory category,
            Target defaultValue) {
        super(option, flag, description, Target.class, multiplicity, stability, scope, availability, category, defaultValue);
    }

    @Override
    public @Nullable Target convert(String value) {
        return Target.fromStringNullable(value);
    }

    @Override
    public List<String> acceptedValues() {
        return ProcessorVersion.getPossibleVersions();
    }
}
