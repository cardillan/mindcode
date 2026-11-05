package io.github.cardillan.mindcode.compiler.optimization.cases;

import io.github.cardillan.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

// Note: to is exclusive
@NullMarked
public record Partition(int from, int to, LogicLabel label) {
    public int size() {
        return to - from;
    }

    public boolean follows(Partition other) {
        return from == other.to;
    }

    public Segment toSegment() {
        return new Segment(SegmentType.SINGLE, from, to, label, size(), label, size());
    }
}
