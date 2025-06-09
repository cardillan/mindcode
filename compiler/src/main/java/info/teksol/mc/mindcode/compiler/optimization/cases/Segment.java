package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

// Note: to is exclusive
@NullMarked
public final class Segment implements Comparable<Segment> {
    private final SegmentType type;
    private final int from;
    private final int to;
    private final LogicLabel majorityLabel;

    private int bisectionSteps;
    private boolean handleNulls;

    public Segment(SegmentType type, int from, int to, LogicLabel majorityLabel) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.majorityLabel = majorityLabel;
    }

    private Segment(SegmentType type, int from, int to, LogicLabel majorityLabel, boolean handleNulls) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.majorityLabel = majorityLabel;
        this.handleNulls = handleNulls;
    }

    public Segment duplicate() {
        return new Segment(type, from, to, majorityLabel, handleNulls);
    }

    public void setBisectionSteps(int bisectionSteps) {
        this.bisectionSteps = bisectionSteps;
    }

    public void setHandleNulls() {
        this.handleNulls = true;
    }

    @Override
    public int compareTo(@NotNull Segment o) {
        return Integer.compare(from, o.from);
    }

    public int size() {
        return to - from;
    }

    public boolean contains(Partition partition) {
        return from < partition.to() && to > partition.from();
    }

    public boolean contains(int value) {
        return from <= value && value < to;
    }

    public boolean follows(Segment other) {
        return from == other.to;
    }

    public SegmentType type() {
        return type;
    }

    public String typeName() {
        return empty() ? "EMPTY" : type.name();
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public boolean empty() {
        return type == SegmentType.SINGLE && majorityLabel == LogicLabel.EMPTY;
    }

    public LogicLabel majorityLabel() {
        return majorityLabel;
    }

    public int bisectionSteps() {
        return bisectionSteps;
    }

    public boolean handleNulls() {
        return handleNulls;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Segment segment = (Segment) o;
        return from == segment.from
                && to == segment.to
                && handleNulls == segment.handleNulls
                && type == segment.type
                && Objects.equals(majorityLabel, segment.majorityLabel);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(type);
        result = 31 * result + from;
        result = 31 * result + to;
        result = 31 * result + Objects.hashCode(majorityLabel);
        result = 31 * result + Boolean.hashCode(handleNulls);
        return result;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "type=" + type +
                ", from=" + from +
                ", to=" + to +
                ", majorityLabel=" + majorityLabel +
                ", handleNulls=" + handleNulls +
                '}';
    }
}
