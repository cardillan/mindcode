package info.teksol.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.util.CollectionUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class CollectionUtilsTest {

    private static final List<String> list = List.of("a", "b", "c", "d", "a", "b", "c");

    @Test
    void inMatches() {
        assertTrue(in("a", "b", "c").test("b"));
    }

    @Test
    void inDoesNotMatch() {
        assertFalse(in("a", "b", "c").test("B"));
    }

    @Test
    void notInMatches() {
        assertTrue(notIn("a", "b", "c").test("B"));
    }

    @Test
    void notInDoesNotMatch() {
        assertFalse(notIn("a", "b", "c").test("b"));
    }

    @Test
    void findFirstIndex_findsIndex() {
        assertEquals(findFirstIndex(list, "a"::equals), 0);
        assertEquals(findFirstIndex(list, "b"::equals), 1);
        assertEquals(findFirstIndex(list, "c"::equals), 2);
    }

    @Test
    void findFirstIndex_reportsNonExisting() {
        assertEquals(findFirstIndex(list, "none"::equals), -1);
    }
}
