package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
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
