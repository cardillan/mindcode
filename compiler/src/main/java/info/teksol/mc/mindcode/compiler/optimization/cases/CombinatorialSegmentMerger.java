package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class CombinatorialSegmentMerger extends AbstractSegmentMerger {
    private static final int MINIMAL_SEGMENT_SIZE = 4;
    private static final int MAX_EXCEPTIONS_WHEN = 2;
    private static final int MAX_EXCEPTIONS_ELSE = 3;

    private final List<Partition> partitions;
    private final int strength;
    private final int iterationDecrease;

    private int configurationCount = 0;

    public CombinatorialSegmentMerger(Targets targets, boolean logicConversion, int strength, int iterationDecrease) {
        partitions = splitToPartitions(targets, logicConversion);
        this.strength = strength;
        this.iterationDecrease = iterationDecrease;
    }

    @Override
    public List<Partition> getPartitions() {
        return partitions;
    }

    public int getConfigurationCount() {
        return configurationCount;
    }

    @Override
    public Set<SegmentConfiguration> createSegmentConfigurations() {
        Set<SegmentConfiguration> configurations = new LinkedHashSet<>();
        // The basic solution: single segment
        configurations.add(new SegmentConfiguration(partitions, List.of()));

        // The other
        if (strength > 0) {
            configurationCount++;
            createSegmentConfigurations(configurations, partitions, List.of(), 0);
        }
        return configurations;
    }

    void createSegmentConfigurations(Collection<SegmentConfiguration> configurations, List<Partition> partitions,
            List<Segment> segments, int depth) {
        int count = depth;

        for (Segment largestSegment : findLargestSegments(partitions)) {
            List<Partition> newPartitions = partitions.stream().filter(s -> !largestSegment.contains(s)).toList();
            List<Segment> newSegments = new ArrayList<>(segments);
            newSegments.add(largestSegment);
            configurations.add(new SegmentConfiguration(this.partitions, newSegments));
            configurationCount++;

            if (!newPartitions.isEmpty()) {
                createSegmentConfigurations(configurations, newPartitions, newSegments, depth + iterationDecrease);
            }

            if (++count >= strength) break;
        }
    }

    // Rules for merging partitions:
    // 1. The number of exceptions in the partition is <= MAX_EXCEPTIONS
    // 2. The merged segment starts and stops with the same label, or has no neighbor at one or both ends
    // 3. The average segment size > 1
    List<Segment> findLargestSegments(List<Partition> partitions) {
        List<Segment> result = new ArrayList<>();

        for (int i = 0; i < partitions.size(); i++) {
            Partition start = partitions.get(i);
            Partition stop = start;
            Partition last = start;

            boolean startGap = i == 0 || partitions.get(i - 1).to() != start.from();
            LogicLabel label = start.label();
            int size = start.size();
            int lastSize = size;
            int count = 1;
            int exceptions = 0;
            int maxExceptions = label == LogicLabel.EMPTY ? MAX_EXCEPTIONS_ELSE : MAX_EXCEPTIONS_WHEN;

            for (int j = i + 1; j < partitions.size(); j++) {
                Partition next = partitions.get(j);

                // A hole in the middle is not allowed
                if (next.from() != last.to()) break;

                size += next.size();
                count++;
                if (!label.equals(next.label())) {
                    exceptions += next.size();
                }

                // Exceeded exceptions
                if (exceptions > maxExceptions) {
                    break;
                }

                if (size > count) {
                    if (next.label().equals(label) || startGap || j == partitions.size() - 1 || partitions.get(j + 1).from() != next.to()) {
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
