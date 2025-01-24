package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@NullMarked
class IntRangeTest {

    @Test
    void detectsOverlaps() {
        IntRange r1 = new IntRange(1, 1);
        IntRange r2 = new IntRange(2, 2);
        IntRange r3 = new IntRange(1, 2);

        Assertions.assertAll(
                () -> Assertions.assertFalse(r1.overlaps(r2), "Wrong indication of overlaps"),
                () -> Assertions.assertFalse(r2.overlaps(r1), "Wrong indication of overlaps"),
                () -> Assertions.assertTrue(r1.overlaps(r3), "Missing indication of overlaps"),
                () -> Assertions.assertTrue(r2.overlaps(r3), "Missing indication of overlaps"),
                () -> Assertions.assertTrue(r3.overlaps(r1), "Missing indication of overlaps"),
                () -> Assertions.assertTrue(r3.overlaps(r2), "Missing indication of overlaps")
        );
    }

    @Test
    void detectContains() {
        IntRange range = new IntRange(2, 5);

        Assertions.assertAll(
                () -> Assertions.assertFalse(range.contains(0), "Wrong indication of contains"),
                () -> Assertions.assertFalse(range.contains(1), "Wrong indication of contains"),
                () -> Assertions.assertTrue(range.contains(2), "Missing indication of contains"),
                () -> Assertions.assertTrue(range.contains(5), "Missing indication of contains"),
                () -> Assertions.assertFalse(range.contains(6), "Wrong indication of contains")
        );
    }
}