package info.teksol.mindcode;

import org.jetbrains.annotations.NotNull;

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

    @Override
    public int compareTo(@NotNull IntRange o) {
        return min == o.min ? Integer.compare(max, o.max) : Integer.compare(min, o.min);
    }
}
