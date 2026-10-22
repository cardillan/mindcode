package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.stream.IntStream;

/// Integer range, closed on both ends: index is in range iff min <= index <= max.
@NullMarked
public record IntRange(int min, int max) implements Comparable<IntRange> {

    public IntRange {
        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }
    }

    public int start() {
        return min;
    }

    /// @return the exclusive end - first value not belonging to the range.
    public int end() {
        return max + 1;
    }

    public static IntRange closed(int min, int max) {
        return new IntRange(min, max);
    }

    public static IntRange halfOpen(int min, int end) {
        return new IntRange(min, end - 1);
    }

    public boolean overlaps(IntRange other) {
        return other.min <= max && other.max >= min;
    }

    public boolean contains(int value) {
        return value >= min && value <= max;
    }

    public int size() {
        return max - min + 1;
    }

    public String getRangeString() {
        return min == max ? String.valueOf(min) : min + " to " + max;
    }

    public IntRange offset(int offset) {
        return new IntRange(min + offset, max + offset);
    }

    public IntStream stream() {
        return IntStream.range(min, max + 1);
    }

    public <T> List<T> sublist(List<T> list) {
        return list.subList(min, end());
    }

    @Override
    public int compareTo(IntRange o) {
        return min == o.min ? Integer.compare(max, o.max) : Integer.compare(min, o.min);
    }
}
