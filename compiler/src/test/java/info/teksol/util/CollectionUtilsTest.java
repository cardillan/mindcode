package info.teksol.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionUtilsTest {

    private static final List<String> list = List.of("a", "b", "c", "d", "a", "b", "c");

    @Test
    void findFirstIndex_findsIndex() {
        assertEquals(CollectionUtils.findFirstIndex(list, "a"::equals), 0);
        assertEquals(CollectionUtils.findFirstIndex(list, "b"::equals), 1);
        assertEquals(CollectionUtils.findFirstIndex(list, "c"::equals), 2);
    }

    @Test
    void findFirstIndex_reportsNonExisting() {
        assertEquals(CollectionUtils.findFirstIndex(list, "none"::equals), -1);
    }
}
