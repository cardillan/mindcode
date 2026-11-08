package io.github.cardillan.mindcode.toolapp.updates;

import io.github.cardillan.mindcode.Version;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UpdateCheckerTest {
    private static final String RELEASES_FILE = "releases.json";

    @Test
    void releasesCanBeParsed() {
        assertDoesNotThrow(() -> Releases.read(Path.of(RELEASES_FILE)));
    }

    @Test
    void releasesContainVersion() throws IOException {
        Releases releases = Releases.read(Path.of(RELEASES_FILE));
        boolean containsCurrentVersion = releases.stream().anyMatch(r -> r.version().equals(Version.getVersionObject()));
        assertTrue(containsCurrentVersion);
    }

    @Test
    void noDuplicateVersions() throws IOException {
        Map<Version, Long> versions = Releases.read(Path.of(RELEASES_FILE)).stream()
                .map(Release::version)
                .collect(Collectors.groupingBy(v -> v, Collectors.counting()));

        versions.forEach((v, c) -> assertEquals(1, c.longValue(), "Duplicate version " + v + " found"));
    }
}
