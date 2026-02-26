package info.teksol.mc.profile.options;

import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public final class Target {
    private final ProcessorVersion version;
    private final ProcessorType type;

    public Target(ProcessorVersion version, ProcessorType type) {
        this.version = version;
        this.type = type.isSupportedBy(version) ? type : ProcessorType.MICRO_PROCESSOR;
    }

    public static Target fromString(String value) {
        Target target = fromStringNullable(value);

        if (target == null) {
            throw new IllegalArgumentException("Invalid target: " + value);
        }

        return target;
    }

    public static @Nullable Target fromStringNullable(String value) {
        char code = value.charAt(value.length() - 1);
        ProcessorType type = Character.isDigit(code) ? ProcessorType.NO_PROCESSOR : ProcessorType.byCode(code);
        ProcessorVersion version = ProcessorVersion.byCode(value.substring(
                value.startsWith("ML") ? 2 : 0,
                value.length() - (type == null || Character.isDigit(code) ? 0 : 1)));

        if (version == null) {
            return null;
        }

        return new Target(version, type != null ? type : ProcessorType.S);
    }

    public ProcessorVersion version() {
        return version;
    }

    public ProcessorType type() {
        return type;
    }

    public boolean atLeast(ProcessorVersion min) {
        return version.atLeast(min);
    }

    public boolean atMost(ProcessorVersion max) {
        return version.atMost(max);
    }

    public boolean matches(ProcessorVersion min, ProcessorVersion max) {
        return version.matches(min, max);
    }

    /// A target is compatible with another target if code compiled under the target works the same when compiled
    /// under the other target. The relation is not reflexive: 7.1 is compatible with 8.0, but 8.0 is not compatible
    /// with 7.1. Also, 7 is compatible with 7w, but 7w is not compatible with 7.
    ///
    /// @return true if this target is compatible with the given target
    public boolean isCompatibleWith(Target other) {
        return version.isCompatibleWith(other.version) && type.isCompatibleWith(other.type);
    }

    public Target withType(ProcessorType type) {
        return new Target(version, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Target) obj;
        return Objects.equals(this.version, that.version) &&
               Objects.equals(this.type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, type);
    }

    public String targetName() {
        return version.versionName() + type.code();
    }

    public String webpageTargetName() {
        return version.major + (type == ProcessorType.S ? "" : type.code());
    }

    @Override
    public String toString() {
        return "Target[" +
               "version=" + version + ", " +
               "type=" + type + ']';
    }
}
