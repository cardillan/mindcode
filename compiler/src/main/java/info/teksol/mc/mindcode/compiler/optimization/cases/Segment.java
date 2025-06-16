package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static Segment fromPartitions(SegmentType type, List<Partition> partitions) {
        // List sizes 1 to 3 handled separately to avoid costly computation of majority labels
        // in cases where it can be done cheaply.
        if (partitions.size() == 1) {
            Partition partition = partitions.getFirst();
            return new Segment(type, partition.from(), partition.to(), partition.label(), partition.size());
        } else if (partitions.size() == 2) {
            Partition first =  partitions.getFirst();
            Partition last =  partitions.getLast();
            if (first.label() != LogicLabel.INVALID && (first.size() >= last.size() || last.label() == LogicLabel.INVALID)) {
                return new Segment(type, first.from(), last.to(), first.label(), first.size());
            } else {
                return new Segment(type, first.from(), last.to(), last.label(), last.size());
            }
        } else if (partitions.size() == 3 && partitions.getFirst().label() == partitions.getLast().label()) {
            Partition first =  partitions.getFirst();
            Partition middle =  partitions.get(1);
            Partition last =  partitions.getLast();
            int outerSize = first.size() + last.size();

            if (first.label() != LogicLabel.INVALID && (outerSize >= middle.size() || middle.label() == LogicLabel.INVALID)) {
                return new Segment(type, first.from(), last.to(), first.label(), outerSize);
            } else {
                return new Segment(type, first.from(), last.to(), middle.label(), middle.size());
            }
        } else {
            Map<LogicLabel, Integer> sizes = partitions.stream()
                    .collect(Collectors.groupingBy(Partition::label, Collectors.summingInt(Partition::size)));

            LogicLabel majorityLabel = sizes.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(LogicLabel.EMPTY);

            int majoritySize = sizes.getOrDefault(majorityLabel, 0);

            return new Segment(type, partitions.getFirst().from(), partitions.getLast().to(), majorityLabel, majoritySize);
        }
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
