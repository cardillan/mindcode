package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jetbrains.annotations.NotNull;

// Note: to is exclusive
public record Segment(SegmentType type, int from, int to, LogicLabel majorityLabel) implements Comparable<Segment> {
    @Override
    public int compareTo(@NotNull Segment o) {
        return Integer.compare(from, o.from);
    }

    public int size() {
        return to - from;
    }

    public boolean contains(Segment other) {
        return from < other.to && to > other.from;
    }

    public boolean touches(Segment other) {
        return from <= other.to && to >= other.from;
    }

    public boolean contains(int value) {
        return from <= value && value < to;
    }

    public boolean follows(Segment other) {
        return from == other.to;
    }
}
