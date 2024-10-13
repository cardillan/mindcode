package info.teksol.mindcode;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Order(99)
class VersionTest {

    @Test
    void readsVersion() {
        assertDoesNotThrow(Version::getVersion);
    }
}