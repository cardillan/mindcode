package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// Note: to is exclusive
public final class Segment implements Comparable<Segment> {
    private final SegmentType type;
    private final int from;
    private final int to;
    private final LogicLabel majorityLabel;

    private int priorElseValues;
    private boolean padToZero;
    private boolean handleNulls;
    private boolean last;

    public Segment(SegmentType type, int from, int to, LogicLabel majorityLabel) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.majorityLabel = majorityLabel;
    }

    public Segment duplicate() {
        return new Segment(type, from, to, majorityLabel);
    }

    public void setPriorElseValues(int priorElseValues) {
        this.priorElseValues = priorElseValues;
    }

    public void setPadToZero() {
        this.padToZero = true;
    }

    public void setHandleNulls() {
        this.handleNulls = true;
    }

    public void setLast() {
        this.last = true;
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

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public LogicLabel majorityLabel() {
        return majorityLabel;
    }

    public int priorElseValues() {
        return priorElseValues;
    }

    public boolean padToZero() {
        return padToZero;
    }

    public boolean handleNulls() {
        return handleNulls;
    }

    public boolean last() {
        return last;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Segment segment = (Segment) o;
        return from == segment.from && to == segment.to && priorElseValues == segment.priorElseValues && padToZero == segment.padToZero && handleNulls == segment.handleNulls && last == segment.last && type == segment.type && Objects.equals(majorityLabel, segment.majorityLabel);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(type);
        result = 31 * result + from;
        result = 31 * result + to;
        result = 31 * result + Objects.hashCode(majorityLabel);
        result = 31 * result + priorElseValues;
        result = 31 * result + Boolean.hashCode(padToZero);
        result = 31 * result + Boolean.hashCode(handleNulls);
        result = 31 * result + Boolean.hashCode(last);
        return result;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "type=" + type +
                ", from=" + from +
                ", to=" + to +
                ", majorityLabel=" + majorityLabel +
                ", priorElseValues=" + priorElseValues +
                ", padToZero=" + padToZero +
                ", handleNulls=" + handleNulls +
                ", last=" + last +
                '}';
    }
}
