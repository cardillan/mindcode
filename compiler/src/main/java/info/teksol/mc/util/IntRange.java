package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record IntRange(int min, int max) implements Comparable<IntRange> {

    public IntRange {
        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }
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

    @Override
    public int compareTo(IntRange o) {
        return min == o.min ? Integer.compare(max, o.max) : Integer.compare(min, o.min);
    }
}
