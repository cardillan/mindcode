package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static info.teksol.mc.mindcode.compiler.optimization.cases.SegmentType.MIXED;
import static info.teksol.mc.mindcode.compiler.optimization.cases.SegmentType.SINGLE;

// Note: to is exclusive
@NullMarked
public final class Segment implements Comparable<Segment> {
    private final SegmentType type;
    private final int from;
    private final int to;
    private final LogicLabel majorityLabel;
    private final int majoritySize;
    private final LogicLabel endLabel;
    private final int endLabelWeight;

    /// The else path of this segment needs to handle nulls
    private boolean handleNulls;

    /// Bisection depth
    private int depth;

    /// Resolved by the bisection jump
    private boolean inline;

    /// Instead of jumping to a target, this segment will be followed by the corresponding body
    private boolean direct;

    private final int hashCode;

    public Segment(SegmentType type, int from, int to, LogicLabel majorityLabel, int majoritySize, LogicLabel endLabel, int endLabelWeight) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.majorityLabel = majorityLabel;
        this.majoritySize = majoritySize;
        this.endLabel = endLabel;
        this.endLabelWeight = endLabelWeight;
        this.hashCode = computeHash();
    }

    private Segment(SegmentType type, int from, int to, LogicLabel majorityLabel, int majoritySize, LogicLabel endLabel, int endLabelWeight,
            boolean handleNulls, boolean inline) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.majorityLabel = majorityLabel;
        this.majoritySize = majoritySize;
        this.endLabel = endLabel;
        this.endLabelWeight = endLabelWeight;
        this.handleNulls = handleNulls;
        this.inline = inline;
        this.hashCode = computeHash();
    }

    public static Segment empty(int from, int to) {
        int size = to - from;
        return new Segment(SINGLE, from, to, LogicLabel.EMPTY, size,  LogicLabel.EMPTY, size);
    }

    /// Creates a segment from a list of partitions.
    ///
    /// @param type A type of segment to create; if the partition size is 1, SINGLE is used instead.
    /// @param partitions A list of partitions to create a segment from
    public static Segment fromPartitions(SegmentType type, List<Partition> partitions) {
        if (type == SINGLE) throw new IllegalArgumentException("SINGLE is not allowed here");

        // List sizes 1 to 3 handled separately to avoid costly computation of majority labels
        // in cases where it can be done cheaply.
        if (partitions.size() == 1) {
            return partitions.getFirst().toSegment();
        } else if (partitions.size() == 2) {
            Partition first = partitions.getFirst();
            Partition last = partitions.getLast();
            if (first.label() != LogicLabel.INVALID && (first.size() >= last.size() || last.label() == LogicLabel.INVALID)) {
                // For mixed segment, the end label is the majority label (first); for jump table, it is the last partition's label by definition
                LogicLabel endLabel = type == MIXED ? first.label() : last.label();
                // For mixed segment, the end weight is the majority weight; for jump table, it is always 1
                int endLabelWeight = type == MIXED ? first.size() : 1;
                return new Segment(type, first.from(), last.to(), first.label(), first.size(), endLabel, endLabelWeight);
            } else {
                // For mixed segment, the end label is the majority label (last); for jump table, it is the last partition's label by definition
                // --> same in both cases.
                // For mixed segment, the end weight is the majority weight; for jump table, it is always 1
                int endLabelWeight = type == MIXED ? first.size() : 1;
                return new Segment(type, first.from(), last.to(), last.label(), last.size(), last.label(), endLabelWeight);
            }
        } else if (partitions.size() == 3 && partitions.getFirst().label() == partitions.getLast().label()) {
            Partition first =  partitions.getFirst();
            Partition middle =  partitions.get(1);
            Partition last =  partitions.getLast();
            int outerSize = first.size() + last.size();

            if (first.label() != LogicLabel.INVALID && (outerSize >= middle.size() || middle.label() == LogicLabel.INVALID)) {
                // For mixed segment, the end label is the majority label (first and last); for jump table, it is the last partition's label by definition
                // --> same in both cases.
                // For mixed segment, the end weight is the majority weight; for jump table, it is always 1
                int endLabelWeight = type == MIXED ? outerSize : 1;
                return new Segment(type, first.from(), last.to(), last.label(), outerSize, last.label(), endLabelWeight);
            } else {
                // For mixed segment, the end label is the majority label (middle); for jump table, it is the last partition's label by definition
                LogicLabel endLabel = type == MIXED ? middle.label() : last.label();
                // For mixed segment, the end weight is the majority weight; for jump table, it is always 1
                int endLabelWeight = type == MIXED ? middle.size() : 1;
                return new Segment(type, first.from(), last.to(), middle.label(), middle.size(), endLabel, endLabelWeight);
            }
        } else {
            Map<LogicLabel, Integer> sizes = partitions.stream()
                    .collect(Collectors.groupingBy(Partition::label, Collectors.summingInt(Partition::size)));

            LogicLabel majorityLabel = sizes.entrySet().stream()
                    .filter(e -> e.getKey() != LogicLabel.INVALID)
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(LogicLabel.EMPTY);

            int majoritySize = sizes.getOrDefault(majorityLabel, 0);

            // For mixed segment, the end label is the majority label; for jump table, it is the last partition's label by definition
            LogicLabel endLabel = type == MIXED ? majorityLabel : partitions.getLast().label();
            // For mixed segment, the end weight is the majority weight; for jump table, it is always 1
            int endLabelWeight = type == MIXED ? majoritySize : 1;
            return new Segment(type, partitions.getFirst().from(), partitions.getLast().to(), majorityLabel, majoritySize, endLabel, endLabelWeight);
        }
    }

    public Segment convertToMixed() {
        return new Segment(MIXED, from, to, majorityLabel, majoritySize, majorityLabel, majoritySize);
    }

    public Segment duplicate() {
        return new Segment(type, from, to, majorityLabel, majoritySize, endLabel, endLabelWeight, handleNulls, inline);
    }

    public Segment padLow(int from) {
        int diff = majorityLabel == LogicLabel.EMPTY ? this.from - from : 0;
        return new Segment(type, from, to, majorityLabel, majoritySize + diff, endLabel, endLabelWeight, handleNulls, inline);
    }

    public Segment padHigh(int to) {
        int diff = majorityLabel == LogicLabel.EMPTY ? to - this.to : 0;
        return new Segment(type, from, to, majorityLabel, majoritySize + diff, LogicLabel.EMPTY, endLabelWeight, handleNulls, inline);
    }

    public void setBisectionSteps(int depth, boolean inline) {
        this.depth = depth;
        this.inline = inline;
    }

    public void setHandleNulls() {
        this.handleNulls = true;
    }

    public void setDirect() {
        this.direct = true;
    }

    public void resetDirect() {
        this.direct = false;
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
        return type == SINGLE && majorityLabel == LogicLabel.EMPTY;
    }

    public boolean zero() {
        return type == SINGLE && majorityLabel == LogicLabel.INVALID;
    }

    public LogicLabel majorityLabel() {
        return majorityLabel;
    }

    public int majoritySize() {
        return majoritySize;
    }

    public LogicLabel endLabel() {
        return endLabel;
    }

    public int endLabelWeight() {
        return endLabelWeight;
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

    public boolean direct() {
        return direct;
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

    private int computeHash() {
        int result = type.hashCode();
        result = 31 * result + from;
        result = 31 * result + to;
        return result;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "type=" + type +
                ", from=" + from +
                ", to=" + to +
                ", majorityLabel=" + majorityLabel +
                ", majoritySize=" + majoritySize +
                ", endLabel=" + endLabel +
                ", endLabelWeight=" + endLabelWeight +
                ", depth=" + depth +
                ", handleNulls=" + handleNulls +
                ", inline=" + inline +
                ", direct=" + direct +
                '}';
    }
}
