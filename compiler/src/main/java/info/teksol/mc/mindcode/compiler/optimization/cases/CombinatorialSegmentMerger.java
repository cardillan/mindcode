package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;

import java.util.*;

public class CombinatorialSegmentMerger implements SegmentMerger {
    private static final int MAX_SEGMENTS = 6;
    private static final int SEARCH_DECREASE = 1;

    @Override
    public Set<MergedSegments> createMergeConfigurations(List<Segment> segments) {
        Set<MergedSegments> configurations = new LinkedHashSet<>();
        createMergeConfigurations(configurations, segments, List.of(), MAX_SEGMENTS);
        return configurations;
    }

    void createMergeConfigurations(Collection<MergedSegments> configurations, List<Segment> singleSegments,
            List<Segment> mergedSegments, int maxConfigurations) {
        int count = 0;

        for (Segment largestSegment : findLargestSegments(singleSegments)) {
            List<Segment> singleSegments0 = singleSegments.stream().filter(s -> !largestSegment.contains(s)).toList();
            List<Segment> mergedSegments0 = new ArrayList<>(mergedSegments);
            mergedSegments0.add(largestSegment);
            configurations.add(new MergedSegments(mergedSegments0));

            if (!singleSegments0.isEmpty()) {
                createMergeConfigurations(configurations, singleSegments0, mergedSegments0,
                        Math.max(1, maxConfigurations - SEARCH_DECREASE));
            }

            if (++count >= maxConfigurations) break;
        }
    }

    // Rules for merging segments:
    // 1. The number of exceptions in the segment is <= MAX_EXCEPTIONS
    // 2. The merged segment starts and stops with the same label, or has no neighbor at one or both ends
    // 3. The average segment size > 1
    List<Segment> findLargestSegments(List<Segment> segments) {
        List<Segment> result = new ArrayList<>();

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

            if (stop.to() - start.from() >= MINIMAL_SEGMENT_SIZE) {
                SegmentType type = start == stop ? SegmentType.SINGLE : SegmentType.MIXED;
                result.add(new Segment(type, start.from(), stop.to(), label));
            }
        }

        result.sort(Comparator.comparing(Segment::size).reversed());
        return result;
    }
}
