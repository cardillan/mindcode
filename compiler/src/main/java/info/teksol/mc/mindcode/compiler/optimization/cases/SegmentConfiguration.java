package info.teksol.mc.mindcode.compiler.optimization.cases;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NullMarked
public final class SegmentConfiguration {
    /// Contains the original partitions
    private final List<Partition> partitions;

    /// Contains a list of segments created by merging partitions. Each segment is a contiguous range of partitions;
    /// individual segments in a configuration do not overlap.
    private final List<Segment> segments;

    /// Forces creation of a single segment from all partitions
    private final boolean singleSegment;

    public SegmentConfiguration(List<Partition> partitions) {
        this.partitions = partitions;
        this.segments = List.of();
        singleSegment = true;
    }

    public SegmentConfiguration(List<Partition> partitions, List<Segment> segments) {
        this.partitions = partitions;
        this.segments = segments;
        singleSegment = false;
    }

    public List<Partition> getPartitions() {
        return partitions;
    }

    public List<Segment> getMergedSegments() {
        return segments;
    }

    public List<Segment> createSegments(boolean removeRangeCheck, boolean handleNulls, boolean symbolicLabels, CaseStatement caseStatement) {
        if (singleSegment) {
            return List.of(Segment.jumpTable(partitions));
        }

        List<Segment> result = new ArrayList<>(segments);
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
            Segment segment = Segment.fromPartitions(SegmentType.JUMP_TABLE, partitions.subList(i, j));
            result.add(verifyJumpTable(segment, removeRangeCheck, symbolicLabels));
            i = j;
        }

        result.sort(null);
        if (!removeRangeCheck) {
            caseStatement.addLimitSegments(result);
        }

        // Null handling:
        // NULL    ZERO    Handling
        //  No      No     None - nulls go to the else branch
        //  Yes     No     --> Handler at the `else` branch, needs routing in segments <--
        //  No      Yes    Handler at the `zero` branch, no handling in segments
        //  Yes     Yes    Handler at the `zero` branch, no handling in segments
        if (handleNulls && caseStatement.hasNullKey() && !caseStatement.hasZeroKey()) {
            result.stream().filter(s -> s.from() >= 0).findFirst().ifPresent(Segment::setHandleNulls);
        }

        return result;
    }

    private Segment verifyJumpTable(Segment segment, boolean removeRangeCheck, boolean symbolicLabels) {
        if (!removeRangeCheck && segment.type() == SegmentType.JUMP_TABLE) {
            int minJumpTableSize = symbolicLabels && segment.from() != 0 ? 4 : 3;
            int minorityTargets = segment.size() - segment.majoritySize();
            if (minorityTargets < minJumpTableSize) {
                return segment.convertToMixed();
            }
        }
        return segment;
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
