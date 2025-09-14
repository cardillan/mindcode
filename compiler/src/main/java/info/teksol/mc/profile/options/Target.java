package info.teksol.mc.profile.options;

import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public final class Target {
    private final ProcessorVersion version;
    private final ProcessorEdition edition;

    public Target(ProcessorVersion version, ProcessorEdition edition) {
        this.version = version;
        this.edition = edition;
    }

    public Target(String value) {
        ProcessorEdition edition = ProcessorEdition.byCode(value.charAt(value.length() - 1));
        ProcessorVersion version = ProcessorVersion.byCode(value.substring(
                value.startsWith("ML") ? 2 : 0,
                value.length() - (edition == null ? 0 : 1)));

        if (version == null) {
            throw new IllegalArgumentException("Invalid target: " + value);
        }

        this.version = version;
        this.edition = edition != null ? edition : ProcessorEdition.S;
    }

    public ProcessorVersion version() {
        return version;
    }

    public ProcessorEdition edition() {
        return edition;
    }

    public Target withVersion(ProcessorVersion version) {
        return new Target(version, edition);
    }

    public Target withEdition(ProcessorEdition edition) {
        return new Target(version, edition);
    }

    /**
     * A target is compatible with another target if code compiled under the target works the same when compiled
     * under the other target. The relation is not reflexive: 7.1 is compatible with 8.0, but 8.0 is not compatible
     * with 7.1. Also, 7 is compatible with 7w, but 7w is not compatible with 7.
     *
     * @return true if this target is compatible with the given target
     */
    public boolean isCompatibleWith(Target other) {
        return version.isCompatibleWith(other.version) &&
               edition.isCompatibleWith(other.edition);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Target) obj;
        return Objects.equals(this.version, that.version) &&
               Objects.equals(this.edition, that.edition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, edition);
    }

    public String targetName() {
        return version.versionName() + edition.editionName();
    }

    @Override
    public String toString() {
        return "Target[" +
               "version=" + version + ", " +
               "edition=" + edition + ']';
    }
}
