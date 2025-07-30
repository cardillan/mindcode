package info.teksol.mc.profile.options;

import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
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
        ProcessorEdition edition = ProcessorEdition.byCode(value.charAt(value.length() - 1));
        ProcessorVersion version = ProcessorVersion.byCode(value.substring(
                value.startsWith("ML") ? 2 : 0,
                value.length() - (edition == null ? 0 : 1)));

        return version == null ? null : new Target(version, edition != null ? edition : ProcessorEdition.S);
    }

    @Override
    public List<String> acceptedValues() {
        return ProcessorVersion.getPossibleVersions();
    }
}
