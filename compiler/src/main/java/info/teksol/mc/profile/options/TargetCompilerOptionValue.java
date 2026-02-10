package info.teksol.mc.profile.options;

import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
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
        char code = value.charAt(value.length() - 1);
        ProcessorType type = Character.isDigit(code) ? ProcessorType.NO_PROCESSOR : ProcessorType.byCode(code);
        ProcessorVersion version = ProcessorVersion.byCode(value.substring(
                value.startsWith("ML") ? 2 : 0,
                value.length() - (type == null || Character.isDigit(code) ? 0 : 1)));

        return version == null ? null : new Target(version, type != null ? type : ProcessorType.S);
    }

    @Override
    public List<String> acceptedValues() {
        return ProcessorVersion.getPossibleVersions();
    }
}
