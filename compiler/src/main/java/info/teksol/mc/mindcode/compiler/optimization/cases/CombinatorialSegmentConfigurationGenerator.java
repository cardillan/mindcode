package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class CombinatorialSegmentConfigurationGenerator extends AbstractSegmentConfigurationGenerator {
    private static final int MINIMAL_SEGMENT_SIZE = 4;
    private static final int MAX_EXCEPTIONS_WHEN = 2;
    private static final int MAX_EXCEPTIONS_ELSE = 3;

    private final List<Partition> partitions;
    private final int strength;

    private int configurationCount = 0;

    public CombinatorialSegmentConfigurationGenerator(CaseStatement caseStatement, boolean handleNulls, int strength) {
        this.partitions = splitToPartitions(caseStatement, handleNulls);
        this.strength = (1 << strength) >> 1;
    }

    @Override
    public List<Partition> getPartitions() {
        return partitions;
    }

    @Override
    public Set<SegmentConfiguration> createSegmentConfigurations() {
        Set<SegmentConfiguration> configurations = new LinkedHashSet<>();
        // The basic solution: single segment (single jump table)
        configurations.add(new SegmentConfiguration(partitions, List.of()));

        // The other
        if (strength > 0) {
            configurationCount++;
            createSegmentConfigurations(configurations, partitions, List.of(), 0);

            // Add configuration for full bisection search
            List<Segment> allSegments = partitions.stream().map(Partition::toSegment).toList();
            configurations.add(new SegmentConfiguration(partitions, allSegments));
        }

        return configurations;
    }

    void createSegmentConfigurations(Collection<SegmentConfiguration> configurations, List<Partition> partitions,
            List<Segment> segments, int depth) {
        int limit = strength >> depth;
        if (limit == 0) return;

        List<PartitionSelection> selections = new ArrayList<>();
        findLargestSegments(partitions, selections);
        findIsolatedSegments(partitions, selections);

        List<Segment> selected = selections.stream()
                .sorted(Comparator.comparingInt(PartitionSelection::size).reversed())
                .limit(limit)
                .map(p -> Segment.fromPartitions(SegmentType.MIXED, p.partitions))
                .toList();

        if (selected.isEmpty()) return;

        for (Segment segment : selected) {
            List<Segment> newSegments = new ArrayList<>(segments);
            newSegments.add(segment);
            configurations.add(new SegmentConfiguration(this.partitions, newSegments));
            configurationCount++;

            if (limit > 1) {
                List<Partition> newPartitions = partitions.stream().filter(p -> !segment.contains(p)).toList();
                if (!newPartitions.isEmpty()) {
                    createSegmentConfigurations(configurations, newPartitions, newSegments, depth + 1);
                }
            }
        }
    }

    // Rules for merging partitions:
    // 1. The number of exceptions in the partition is <= MAX_EXCEPTIONS
    // 2. The merged segment starts and stops with the same label, or has no neighbor at one or both ends
    // 3. The average segment size > 1
    private void findLargestSegments(List<Partition> partitions, List<PartitionSelection> selections) {
        for (int start = 0; start < partitions.size(); start++) {
            int stop = start;
            Partition last = partitions.get(start);

            boolean startGap = start == 0 || partitions.get(start - 1).to() != last.from();
            LogicLabel label = last.label();
            int size = last.size();
            int count = 1;
            int exceptions = 0;
            int maxExceptions = label == LogicLabel.EMPTY ? MAX_EXCEPTIONS_ELSE : MAX_EXCEPTIONS_WHEN;

            for (int j = start + 1; j < partitions.size(); j++) {
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
                        stop = j;
                    }
                }

                last = next;
            }


            int newSize = partitions.get(stop).to() - partitions.get(start).from();
            if (newSize >= MINIMAL_SEGMENT_SIZE) {
                List<Partition> newPartitions = partitions.subList(start, stop + 1);
                selections.add(new PartitionSelection(newPartitions, newSize));
            }
        }
    }

    private void findIsolatedSegments(List<Partition> partitions, List<PartitionSelection> selections) {
        for (int i = 0; i < partitions.size(); i++) {
            if (partitions.get(i).size() == 1 && partitions.get(i).label() != LogicLabel.EMPTY) {
                int count = (i == 0 || i == partitions.size() - 1) ? 1 : 2;
                selections.add(new PartitionSelection(List.of(partitions.get(i)),
                        ((i > 0 ? partitions.get(i - 1).size() : 0) + ((i < partitions.size() - 1) ? partitions.get(i + 1).size() : 0)) / count));
            }
        }
    }

    private record PartitionSelection(List<Partition> partitions, int size) {}
}
