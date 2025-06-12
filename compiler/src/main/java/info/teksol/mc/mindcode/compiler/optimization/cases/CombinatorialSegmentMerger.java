package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class CombinatorialSegmentMerger extends AbstractSegmentMerger {
    private static final int MINIMAL_SEGMENT_SIZE = 4;
    private static final int MAX_EXCEPTIONS_WHEN = 2;
    private static final int MAX_EXCEPTIONS_ELSE = 3;

    private final List<Partition> partitions;
    private final int strengthLarge;
    private final int strengthSmall;
    private final int iterationDecrease;

    private int configurationCount = 0;

    public CombinatorialSegmentMerger(Targets targets, boolean logicConversion, int strengthLarge, int iterationDecrease) {
        this.partitions = splitToPartitions(targets, logicConversion);
        this.strengthLarge = strengthLarge;
        this.iterationDecrease = iterationDecrease;

        // Density is the average number of targets per branch
        double density = (double) targets.size() / targets.getTargetCount();

        // High density case expressions generate a lot of segment configurations from segment merging, and do not
        // benefit from isolating small segments. The strength of generating small (isolated) segments is therefore
        // inversely proportional to density. Increasing the multiplication factor increases the number of small
        // segment configurations quite dramatically in some scenarios. Values higher than 6 lead to a very high
        // number of combinations and unacceptable optimization times.
        double multiplicationFactor = 3.0;
        this.strengthSmall = (int) (multiplicationFactor / density);
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
        if (strengthLarge > 0) {
            configurationCount++;
            createSegmentConfigurations(configurations, partitions, List.of(), 0);
        }
        return configurations;
    }

    void createSegmentConfigurations(Collection<SegmentConfiguration> configurations, List<Partition> partitions,
            List<Segment> segments, int depth) {
        int limitLarge = Math.max(1, (strengthLarge - iterationDecrease * depth));
        int limitSmall = Math.max(0, (strengthSmall - 2 * depth));

        List<Segment> merged = CollectionUtils.mergeLists(
                findLargestSegments(partitions, limitLarge),
                findSmallestSegments(partitions, limitSmall));

        for (Segment largestSegment : merged) {
            List<Partition> newPartitions = partitions.stream().filter(s -> !largestSegment.contains(s)).toList();
            List<Segment> newSegments = new ArrayList<>(segments);
            newSegments.add(largestSegment);
            configurations.add(new SegmentConfiguration(this.partitions, newSegments));
            configurationCount++;

            if (!newPartitions.isEmpty()) {
                createSegmentConfigurations(configurations, newPartitions, newSegments, depth + 1);
            }
        }
    }

    // Rules for merging partitions:
    // 1. The number of exceptions in the partition is <= MAX_EXCEPTIONS
    // 2. The merged segment starts and stops with the same label, or has no neighbor at one or both ends
    // 3. The average segment size > 1
    List<Segment> findLargestSegments(List<Partition> partitions, int limit) {
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
                result.add(new Segment(type, start.from(), stop.to(), label, size - exceptions));
            }
        }

        result.sort(Comparator.comparing(Segment::size).reversed());
        return result;
    }

    List<Segment> findSmallestSegments(List<Partition> partitions, int limit) {
        List<SingleSegment> singleSegments = new ArrayList<>();

        for (int i = 0; i < partitions.size(); i++) {
            if (partitions.get(i).size() == 1 && partitions.get(i).label() != LogicLabel.EMPTY) {
                singleSegments.add(new SingleSegment(partitions.get(i),
                        (i > 0 ? partitions.get(i - 1).size() : 0) + ((i < partitions.size() - 1) ? partitions.get(i + 1).size() : 0)));
            }
        }

        return singleSegments.stream()
                .sorted(Comparator.comparing(SingleSegment::size).reversed())
                .limit(limit)
                .map(SingleSegment::partition)
                .map(p -> new Segment(SegmentType.SINGLE, p.from(), p.to(), p.label(), 1))
                .toList();
    }

    private record SingleSegment(Partition partition, int size) {}
}
