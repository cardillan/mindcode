package info.teksol.mc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VersionTest {

    @Test
    void readsVersion() {
        Assertions.assertDoesNotThrow(Version::getVersion);
    }
}