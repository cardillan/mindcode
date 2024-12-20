package info.teksol.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
        assertEquals(0, findFirstIndex(list, "a"::equals));
        assertEquals(1, findFirstIndex(list, "b"::equals));
        assertEquals(2, findFirstIndex(list, "c"::equals));
    }

    @Test
    void findFirstIndex_reportsNonExisting() {
        assertEquals(-1, findFirstIndex(list, "none"::equals));
    }

    @Test
    void findFirstIndexWithStartIndex_findsIndex() {
        assertEquals(4, findFirstIndex(list, 3, "a"::equals));
        assertEquals(6, findFirstIndex(list, 3, "c"::equals));
    }

    @Test
    void findFirstIndexWithStartIndex_reportsNonExisting() {
        assertEquals(-1, findFirstIndex(list, 5, "d"::equals));
        assertEquals(-1, findFirstIndex(list, 3, "none"::equals));
    }

    @Test
    void findFirstIndexWithStartIndex_throwsExceptionForInvalidStartIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> findFirstIndex(list, -1, "a"::equals));
        assertThrows(IndexOutOfBoundsException.class, () -> findFirstIndex(list, 8, "b"::equals));
    }

    @Test
    void findLastIndex_findsIndex() {
        assertEquals(4, findLastIndex(list, "a"::equals));
        assertEquals(5, findLastIndex(list, "b"::equals));
        assertEquals(6, findLastIndex(list, "c"::equals));
    }

    @Test
    void findLastIndex_reportsNonExisting_checkAtSpecificIndex() {
        assertEquals(-1, findLastIndex(list, 6, "none"::equals));
        assertEquals(-1, findLastIndex(list, 3, "x"::equals));
    }

    @Test
    void findLastIndex_throwsExceptionForInvalidStartIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> findLastIndex(list, -1, "a"::equals));
        assertThrows(IndexOutOfBoundsException.class, () -> findLastIndex(list, 8, "b"::equals));
    }

    @Test
    void findLastIndex_reportsNonExisting() {
        assertEquals(-1, findLastIndex(list, "none"::equals));
    }

    @Test
    void findLastIndexWithStartIndex_findsIndex() {
        assertEquals(4, findLastIndex(list, 5, "a"::equals));
        assertEquals(2, findLastIndex(list, 3, "c"::equals));
    }

    @Test
    void findLastIndexWithStartIndex_reportsNonExisting() {
        assertEquals(-1, findLastIndex(list, 6, "none"::equals));
        assertEquals(-1, findLastIndex(list, 2, "d"::equals));
    }

    @Test
    void findLastIndexWithStartIndex_throwsExceptionForInvalidStartIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> findLastIndex(list, -1, "a"::equals));
        assertThrows(IndexOutOfBoundsException.class, () -> findLastIndex(list, 7, "b"::equals));
    }

    @Test
    void removeFirstMatching_removesCorrectItem() {
        List<String> testList = new ArrayList<>(List.of("a", "b", "c", "d", "b"));
        assertEquals("b", removeFirstMatching(testList, "b"::equals));
        assertEquals(List.of("a", "c", "d", "b"), testList);
    }

    @Test
    void removeFirstMatching_returnsNullIfNoMatch() {
        List<String> testList = new ArrayList<>(List.of("a", "b", "c"));
        assertNull(removeFirstMatching(testList, "x"::equals));
        assertEquals(List.of("a", "b", "c"), testList);
    }

    @Test
    void removeFirstMatching_doesNotRemoveWhenListIsEmpty() {
        List<String> testList = new ArrayList<>();
        assertNull(removeFirstMatching(testList, "a"::equals));
        assertTrue(testList.isEmpty());
    }
}
