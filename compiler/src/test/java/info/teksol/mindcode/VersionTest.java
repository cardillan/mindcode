package info.teksol.mindcode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class VersionTest {

    @Test
    void readsVersion() {
        assertDoesNotThrow(Version::getVersion);
    }
}