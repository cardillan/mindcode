package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@NullMarked
public final class SegmentConfiguration {
    /// Contains the original partitions
    private final List<Partition> partitions;

    /// Contains a list of segments created by merging partitions. Each segment is a contiguous range of partitions;
    /// individual segments in a configuration do not overlap.
    private final List<Segment> segments;

    public SegmentConfiguration(List<Partition> partitions, List<Segment> segments) {
        this.partitions = partitions;
        this.segments = segments;
    }

    public List<Partition> getPartitions() {
        return partitions;
    }

    public List<Segment> getMergedSegments() {
        return segments;
    }

    public List<Segment> createSegments(boolean removeRangeCheck, boolean handleNulls, Targets targets) {
        List<Segment> result = segments.stream().map(Segment::duplicate).collect(Collectors.toCollection(ArrayList::new));
        List<Partition> partitions = new ArrayList<>(this.partitions);

        // Remove partitions which are already merged
        for (Segment Segment : segments) {
            partitions.removeIf(Segment::contains);
        }

        // Merge the remaining partitions into JUMP_TABLE segments
        int i = 0;
        while (i < partitions.size()) {
            Partition partition = partitions.get(i);
            int j = i + 1;
            while (j < partitions.size() && partitions.get(j).follows(partitions.get(j - 1))) j++;
            List<Partition> mergedPartitions = partitions.subList(i, j);

            Map<LogicLabel, Integer> sizes = mergedPartitions.stream()
                    .collect(Collectors.groupingBy(Partition::label, Collectors.summingInt(Partition::size)));

            LogicLabel majorityLabel = sizes.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(LogicLabel.EMPTY);

            int majoritySize = sizes.getOrDefault(majorityLabel, 0);

            result.add(new Segment(mergedPartitions.size() == 1 ? SegmentType.SINGLE : SegmentType.JUMP_TABLE,
                    mergedPartitions.getFirst().from(), mergedPartitions.getLast().to(), majorityLabel, majoritySize));
            i = j;
        }

        result.sort(null);
        if (!removeRangeCheck) {
            targets.addLimitSegments(result);
        }

        // Null handling:
        // NULL    ZERO    Handling
        //  No      No     None - nulls go to the else branch
        //  Yes     No     --> Handler at the `else` branch, needs routing in segments <--
        //  No      Yes    Handler at the `zero` branch, no handling in segments
        //  Yes     Yes    Handler at the `zero` branch, no handling in segments
        if (handleNulls && targets.hasNullKey() && !targets.hasZeroKey()) {
            result.stream().filter(s -> s.from() >= 0).findFirst().ifPresent(Segment::setHandleNulls);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SegmentConfiguration) obj;
        return Objects.equals(this.segments, that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments);
    }

    @Override
    public String toString() {
        return "SegmentConfiguration{" +
                "singularSegments=" + partitions +
                ", mergedSegments=" + segments +
                '}';
    }
}
