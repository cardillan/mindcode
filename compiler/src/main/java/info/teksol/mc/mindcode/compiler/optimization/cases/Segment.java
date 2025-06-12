package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

// Note: to is exclusive
@NullMarked
public final class Segment implements Comparable<Segment> {
    private final SegmentType type;
    private final int from;
    private final int to;
    private final LogicLabel majorityLabel;
    private final int majoritySize;

    /// The else path of this segment needs to handle nulls
    private boolean handleNulls;

    /// Bisection depth
    private int depth;

    /// Resolved by the bisection jump
    private boolean inline;

    public Segment(SegmentType type, int from, int to, LogicLabel majorityLabel, int majoritySize) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.majorityLabel = majorityLabel;
        this.majoritySize = majoritySize;
    }

    private Segment(SegmentType type, int from, int to, LogicLabel majorityLabel, int majoritySize, boolean handleNulls, boolean inline) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.majorityLabel = majorityLabel;
        this.majoritySize = majoritySize;
        this.handleNulls = handleNulls;
        this.inline = inline;
    }

    public Segment duplicate() {
        return new Segment(type, from, to, majorityLabel, majoritySize, handleNulls, inline);
    }

    public Segment limitLow(int from) {
        int diff = majorityLabel == LogicLabel.EMPTY ? this.from - from : 0;
        return new Segment(type, from, to, majorityLabel, majoritySize + diff, handleNulls, inline);
    }

    public Segment limitHigh(int to) {
        int diff = majorityLabel == LogicLabel.EMPTY ? to - this.to : 0;
        return new Segment(type, from, to, majorityLabel, majoritySize + diff, handleNulls, inline);
    }

    public void setBisectionSteps(int depth, boolean inline) {
        this.depth = depth;
        this.inline = inline;
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

    public int majoritySize() {
        return majoritySize;
    }

    public int depth() {
        return depth;
    }

    public boolean handleNulls() {
        return handleNulls;
    }

    public boolean inline() {
        return inline;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Segment segment = (Segment) o;
        return from == segment.from
                && to == segment.to
                && majoritySize == segment.majoritySize
                && depth == segment.depth
                && handleNulls == segment.handleNulls
                && inline == segment.inline
                && type == segment.type
                && majorityLabel.equals(segment.majorityLabel);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + from;
        result = 31 * result + to;
        result = 31 * result + majorityLabel.hashCode();
        result = 31 * result + majoritySize;
        result = 31 * result + depth;
        result = 31 * result + Boolean.hashCode(handleNulls);
        result = 31 * result + Boolean.hashCode(inline);
        return result;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "type=" + type +
                ", from=" + from +
                ", to=" + to +
                ", majorityLabel=" + majorityLabel +
                ", majoritySize=" + majoritySize +
                ", depth=" + depth +
                ", handleNulls=" + handleNulls +
                ", inline=" + inline +
                '}';
    }
}
