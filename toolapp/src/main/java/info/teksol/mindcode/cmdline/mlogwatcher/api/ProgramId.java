package info.teksol.mindcode.cmdline.mlogwatcher.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class ProgramId {
    @JsonProperty("id_prefix")
    private String idPrefix;

    private int major;

    private int minor;

    private int revision;

    public ProgramId() {

    }

    public String getIdPrefix() {
        return idPrefix;
    }

    public void setIdPrefix(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public ProgramId(String idPrefix, int major, int minor, int revision) {
        this.idPrefix = idPrefix;
        this.major = major;
        this.minor = minor;
        this.revision = revision;
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

    public String toVersionString() {
        return "v" + major + "." + minor + "." + revision;
    }

    @Override
    public String toString() {
        return "ProgramId[" +
                "idPrefix=" + idPrefix + ", " +
                "major=" + major + ", " +
                "minor=" + minor + ", " +
                "revision=" + revision + ']';
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
}
