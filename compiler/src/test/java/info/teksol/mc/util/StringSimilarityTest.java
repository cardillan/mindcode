package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@NullMarked
class StringSimilarityTest {

    @Test
    public void findSimilarWordNone() {
        Optional<String> bestAlternative = StringSimilarity.findBestAlternative("none", "false", "true");
        Assertions.assertEquals("false", bestAlternative.orElseThrow());
    }

    @Test
    public void findSimilarWordYes() {
        Optional<String> bestAlternative = StringSimilarity.findBestAlternative("yes", "false", "true");
        Assertions.assertEquals("true", bestAlternative.orElseThrow());
    }
}