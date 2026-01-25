package info.teksol.mindcode.cmdline.mlogwatcher;

import java.util.Objects;

public final class ProgramId {
    public final String idPrefix;
    public final int major;
    public final int minor;
    public final int revision;

    public ProgramId(String idPrefix, int major, int minor, int revision) {
        this.idPrefix = idPrefix;
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }

    public static ProgramId parse(String id) {
        final String versionPrefix = "\nversion: ";
        int versionStart = id.indexOf(versionPrefix);
        if (versionStart == -1) return null;

        versionStart += versionPrefix.length();
        String idPrefix = id.substring(0, versionStart);
        if (id.charAt(versionStart) == 'v') versionStart++;

        String[] numbers = id.substring(versionStart).split("\\.");
        if (numbers.length > 3) return null;

        try {
            int major = Integer.parseInt(numbers[0]);
            int minor = numbers.length > 1 ? Integer.parseInt(numbers[1]) : 0;
            int revision = numbers.length > 2 ? Integer.parseInt(numbers[2]) : 0;
            return major >= 0 && minor >= 0 && revision >= 0 ? new ProgramId(idPrefix, major, minor, revision) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean shouldUpdate(ProgramId newId) {
        return idPrefix.equals(newId.idPrefix)
                && (newId.major > major
                || newId.major == major && newId.minor > minor
                || newId.major == major && newId.minor == minor && newId.revision >= revision);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ProgramId) obj;
        return Objects.equals(this.idPrefix, that.idPrefix) &&
                this.major == that.major &&
                this.minor == that.minor &&
                this.revision == that.revision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPrefix, major, minor, revision);
    }

    @Override
    public String toString() {
        return "ProgramId[" +
                "idPrefix=" + idPrefix + ", " +
                "major=" + major + ", " +
                "minor=" + minor + ", " +
                "revision=" + revision + ']';
    }

}
