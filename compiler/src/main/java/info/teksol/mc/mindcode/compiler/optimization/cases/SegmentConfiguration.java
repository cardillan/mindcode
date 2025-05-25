package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class SegmentConfiguration {
    private final List<Segment> singularSegments;
    private final List<Segment> mergedSegments;

    public SegmentConfiguration(List<Segment> singularSegments, List<Segment> mergedSegments) {
        this.singularSegments = singularSegments;
        this.mergedSegments = mergedSegments;
    }

    public List<Segment> segments() {
        return mergedSegments;
    }

    public List<Segment> completeSegments(Consumer<String> dbg) {
        List<Segment> result = new ArrayList<>(mergedSegments);
        List<Segment> segments = new ArrayList<>(singularSegments);
        for (Segment mergedSegment : mergedSegments) {
            segments.removeIf(mergedSegment::contains);
        }

        int i = 0;
        while (i < segments.size()) {
            Segment segment = segments.get(i);
            int j = i + 1;
            while (j < segments.size() && segments.get(j).follows(segments.get(j - 1))) j++;
            result.add(new Segment(i == j - 1 ? SegmentType.SINGLE : SegmentType.JUMP_TABLE,
                    segment.from(), segments.get(j - 1).to(), segment.majorityLabel()));
            i = j;
        }

        // Remove completely empty segments
        result.removeIf(segment -> segment.type() == SegmentType.SINGLE && segment.majorityLabel() == LogicLabel.EMPTY);
        result.sort(null);

//        dbg.accept("Singular segments: \n" + singularSegments.stream().map(Object::toString).collect(Collectors.joining("\n")));
//        dbg.accept("Merged segments: \n" + mergedSegments.stream().map(Object::toString).collect(Collectors.joining("\n")));
//        dbg.accept("Result: \n" + result.stream().map(Object::toString).collect(Collectors.joining("\n")));
//        dbg.accept("\n");

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SegmentConfiguration) obj;
        return Objects.equals(this.mergedSegments, that.mergedSegments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mergedSegments);
    }

    @Override
    public String toString() {
        return "SegmentConfiguration{" +
                "singularSegments=" + singularSegments +
                ", mergedSegments=" + mergedSegments +
                '}';
    }
}
