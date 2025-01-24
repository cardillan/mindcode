package info.teksol.mc;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@NullMarked
class VersionTest {

    @Test
    void readsVersion() {
        Assertions.assertDoesNotThrow(Version::getVersion);
    }
}