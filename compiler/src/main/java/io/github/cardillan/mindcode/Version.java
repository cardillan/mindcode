package io.github.cardillan.mindcode;

import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

@NullMarked
public record Version(int major, int minor, int release) implements Comparable<Version> {
    private static final String version = resolveVersion();

    public static String getVersion() {
        return version;
    }

    public static Version getVersionObject() {
        return parse(version);
    }

    public static Version parse(String version) {
        String[] parts = version.split("\\.");
        if (parts.length == 3) {
            try {
                return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Error parsing version numbers.", e);
            }
        }

        throw new IllegalArgumentException("Unsupported version format.");
    }

    public static Optional<Version> parseOptional(String version) {
        String[] parts = version.split("\\.");
        if (parts.length == 3) {
            try {
                return Optional.of(new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    @Override
    public int compareTo(Version other) {
        return major != other.major ? Integer.compare(major, other.major) :
                minor != other.minor ? Integer.compare(minor, other.minor) :
                        Integer.compare(release, other.release);
    }

    private static String resolveVersion() {
        try (InputStream in = Version.class.getResourceAsStream("mindcode.properties")) {
            if (in == null) {
                return "unknown";
            }
            Properties properties = new Properties();
            properties.load(in);
            return properties.getProperty("mindcode.version");
        } catch (IOException e) {
            throw new RuntimeException("Error obtaining version information", e);
        }
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + release;
    }
}
