package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.mimex.ContentType;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class Targets {
    private final boolean hasElseBranch;
    private final Set<LogicLabel> labels = new HashSet<>();
    private final NavigableMap<Integer, LogicLabel> targets = new TreeMap<>();
    public @Nullable LogicLabel nullTarget;        // Handles null only
    public @Nullable LogicLabel elseTarget;        // Handles else only
    public @Nullable LogicLabel nullOrElseTarget;  // Handles else or null

    private int totalSize;
    private int elseValues;

    private @Nullable Segment leadingSegment;
    private @Nullable Segment trailingSegment;

    public Targets(boolean hasElseBranch) {
        this.hasElseBranch = hasElseBranch;
    }

    public void computeElseValues(ContentType contentType, MindustryMetadata metadata, boolean targetSpecificOptimization) {
        totalSize = computeTotalSize(contentType, metadata);
        elseValues = Math.max(totalSize - targets.size(), 0);

        if (contentType == ContentType.UNKNOWN) {
            leadingSegment = new Segment(SegmentType.SINGLE, targets.firstKey(), targets.firstKey(), LogicLabel.EMPTY, 0);
            trailingSegment = new Segment(SegmentType.SINGLE, targets.lastKey() + 1, targets.lastKey() + 1, LogicLabel.EMPTY, 0);
        } else {
            int lastKey = targets.lastKey() + 1;
            boolean limitLow = targets.firstKey() > 0;
            boolean limitHigh = !targetSpecificOptimization || (lastKey < totalSize);
            leadingSegment = limitLow ? new Segment(SegmentType.SINGLE, 0, targets.firstKey(), LogicLabel.EMPTY, targets.firstKey()) : null;
            trailingSegment = limitHigh ? new Segment(SegmentType.SINGLE, lastKey, totalSize, LogicLabel.EMPTY, totalSize - lastKey) : null;
        }
    }

    private int computeTotalSize(ContentType contentType, MindustryMetadata metadata) {
        String lookupKeyword = contentType.getLookupKeyword();
        if (lookupKeyword == null) return targets.size();
        Map<Integer, ?> lookupMap = metadata.getLookupMap(lookupKeyword);
        return lookupMap == null ? targets.size() : lookupMap.size();
    }

    public boolean hasElseBranch() {
        return hasElseBranch;
    }

    public int getTargetCount() {
        return labels.size();
    }

    public int getTotalSize() {
        return totalSize;
    }

    public int getElseValues() {
        return elseValues;
    }

    public void addLimitSegments(List<Segment> segments) {
        if (leadingSegment != null) segments.addFirst(leadingSegment.duplicate());
        if (trailingSegment != null) segments.addLast(trailingSegment.duplicate());
    }

    public boolean hasZeroKey() {
        return targets.containsKey(0);
    }

    public boolean hasNullKey() {
        return nullTarget != null;
    }

    public boolean hasNullOrZeroKey() {
        return nullTarget != null || targets.containsKey(0);
    }

    public @Nullable LogicLabel get(Integer key) {
        return targets.get(key);
    }

    public LogicLabel getExisting(Integer key) {
        return Objects.requireNonNull(targets.get(key));
    }

    public LogicLabel getOrDefault(Integer key, LogicLabel defaultValue) {
        return targets.getOrDefault(key, defaultValue);
    }

    public @Nullable LogicLabel put(@Nullable Integer key, LogicLabel value) {
        labels.add(value);
        if (key == null) {
            LogicLabel previous = nullTarget;
            nullTarget = value;
            return previous;
        } else {
            return targets.put(key, value);
        }
    }

    public boolean isEmpty() {
        return targets.isEmpty();
    }

    public int size() {
        return targets.size();
    }

    public SortedMap<Integer, LogicLabel> subMap(int from, int to) {
        return targets.subMap(from, to);
    }

    public Map.Entry<Integer, LogicLabel> firstEntry() {
        return targets.firstEntry();
    }

    public Map.Entry<Integer, LogicLabel> lastEntry() {
        return targets.lastEntry();
    }

    public Integer firstKey() {
        return targets.firstKey();
    }

    public Integer lastKey() {
        return targets.lastKey();
    }

    public Set<Map.Entry<Integer, LogicLabel>> entrySet() {
        return targets.entrySet();
    }

    public Set<Integer> keySet() {
        return targets.keySet();
    }

    public LogicLabel getElseTarget() {
        return Objects.requireNonNull(elseTarget);
    }

    public LogicLabel getNullOrElseTarget() {
        return Objects.requireNonNull(nullOrElseTarget);
    }

    // Note: if the case expression doesn't contain a null branch, the nullTarget is set to the else branch.
    public LogicLabel getNullTarget() {
        return Objects.requireNonNull(nullTarget);
    }
}
