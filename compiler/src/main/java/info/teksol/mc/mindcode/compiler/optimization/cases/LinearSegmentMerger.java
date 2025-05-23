package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LinearSegmentMerger implements SegmentMerger {

    @Override
    public Set<MergedSegments> createMergeConfigurations(List<Segment> segments) {
        List<Segment> merged = mergeSegments(segments);

        Set<MergedSegments> result = new LinkedHashSet<>();
        for (int i = 1; i <= merged.size(); i++) {
            result.add(new MergedSegments(merged.subList(0, i)));
        }

        return result;
    }

    // Rules for merging segments:
    // 1. The number of exceptions in the segment is <= MAX_EXCEPTIONS
    // 2. The merged segment starts and stops with the same label, or has no neighbor at one or both ends
    // 3. The average segment size > 1
    static Segment findLargestSegment(List<Segment> segments) {
        LogicLabel majorityLabel = LogicLabel.EMPTY;
        SegmentType type = SegmentType.SINGLE;
        int largest = -1;
        int from = 0, to = 0;

        for (int i = 0; i < segments.size(); i++) {
            Segment start = segments.get(i);
            Segment stop = start;
            Segment last = start;

            boolean startGap = i == 0 || segments.get(i - 1).to() != start.from();
            LogicLabel label = start.majorityLabel();
            int size = start.size();
            int lastSize = size;
            int count = 1;
            int exceptions = 0;
            int maxExceptions = label == LogicLabel.EMPTY ? MAX_EXCEPTIONS_ELSE : MAX_EXCEPTIONS_WHEN;

            for (int j = i + 1; j < segments.size(); j++) {
                Segment next = segments.get(j);

                // A hole in the middle is not allowed
                if (next.from() != last.to()) break;

                size += next.size();
                count++;
                if (!label.equals(next.majorityLabel())) {
                    exceptions += next.size();
                }

                // Exceeded exceptions
                if (exceptions > maxExceptions) {
                    break;
                }

                if (size > count) {
                    if (next.majorityLabel().equals(label) || startGap || j == segments.size() - 1 || segments.get(j + 1).from() != next.to()) {
                        // The merged segment may stop here
                        // Last never points at an impossible end segment
                        stop = next;
                        lastSize = size;
                    }
                }

                last = next;
            }

            if (lastSize > largest) {
                majorityLabel = label;
                largest = lastSize;
                from = start.from();
                to = stop.to();
                type = start == stop ? SegmentType.SINGLE : SegmentType.MIXED;
            }

        }

        return new Segment(type, from, to, majorityLabel);
    }

    static List<Segment> mergeSegments(List<Segment> segmentList) {
        List<Segment> segments = new ArrayList<>(segmentList);
        List<Segment> result = new ArrayList<>();

        while (segments.size() > 1) {
            Segment largest = findLargestSegment(segments);
            if (largest.size() < MINIMAL_SEGMENT_SIZE) break;

            result.add(largest);
            segments.removeIf(largest::contains);
        }

        // They're already sorted from largest to smallest
        return result;
    }
}
