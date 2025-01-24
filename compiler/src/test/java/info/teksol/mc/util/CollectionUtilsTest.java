package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static info.teksol.mc.util.CollectionUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@NullMarked
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
    void indexOf_findsIndex() {
        assertEquals(0, indexOf(list, "a"::equals));
        assertEquals(1, indexOf(list, "b"::equals));
        assertEquals(2, indexOf(list, "c"::equals));
    }

    @Test
    void indexOf_reportsNonExisting() {
        assertEquals(-1, indexOf(list, "none"::equals));
    }

    @Test
    void indexOfWithStartIndex_findsIndex() {
        assertEquals(4, indexOf(list, 3, "a"::equals));
        assertEquals(6, indexOf(list, 3, "c"::equals));
    }

    @Test
    void indexOfWithStartIndex_reportsNonExisting() {
        assertEquals(-1, indexOf(list, 5, "d"::equals));
        assertEquals(-1, indexOf(list, 3, "none"::equals));
    }

    @Test
    void indexOfWithStartIndex_throwsExceptionForInvalidStartIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> indexOf(list, -1, "a"::equals));
        assertThrows(IndexOutOfBoundsException.class, () -> indexOf(list, 8, "b"::equals));
    }

    @Test
    void lastIndexOf_findsIndex() {
        assertEquals(4, lastIndexOf(list, "a"::equals));
        assertEquals(5, lastIndexOf(list, "b"::equals));
        assertEquals(6, lastIndexOf(list, "c"::equals));
    }

    @Test
    void lastIndexOf_reportsNonExisting_checkAtSpecificIndex() {
        assertEquals(-1, lastIndexOf(list, 6, "none"::equals));
        assertEquals(-1, lastIndexOf(list, 3, "x"::equals));
    }

    @Test
    void lastIndexOf_throwsExceptionForInvalidStartIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> lastIndexOf(list, -1, "a"::equals));
        assertThrows(IndexOutOfBoundsException.class, () -> lastIndexOf(list, 8, "b"::equals));
    }

    @Test
    void lastIndexOf_reportsNonExisting() {
        assertEquals(-1, lastIndexOf(list, "none"::equals));
    }

    @Test
    void lastIndexOfWithStartIndex_findsIndex() {
        assertEquals(4, lastIndexOf(list, 5, "a"::equals));
        assertEquals(2, lastIndexOf(list, 3, "c"::equals));
    }

    @Test
    void lastIndexOfWithStartIndex_reportsNonExisting() {
        assertEquals(-1, lastIndexOf(list, 6, "none"::equals));
        assertEquals(-1, lastIndexOf(list, 2, "d"::equals));
    }

    @Test
    void lastIndexOfWithStartIndex_throwsExceptionForInvalidStartIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> lastIndexOf(list, -1, "a"::equals));
        assertThrows(IndexOutOfBoundsException.class, () -> lastIndexOf(list, 7, "b"::equals));
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
