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
    private final Set<LogicLabel> movableLabels = new HashSet<>();
    private final NavigableMap<Integer, LogicLabel> targets = new TreeMap<>();
    private boolean hasNullKey;
    private boolean hasZeroKey;
    public @Nullable LogicLabel nullTarget;        // Handles null only
    public @Nullable LogicLabel elseTarget;        // Handles else only
    public @Nullable LogicLabel nullOrElseTarget;  // Handles else or null

    /// For a given key, contains the number of targets with values less than the key
    private final NavigableMap<Integer, Integer> targetCount = new TreeMap<>();
    private int totalSize;
    private int elseValues;

    private @Nullable Segment leadingSegment;
    private @Nullable Segment trailingSegment;

    public Targets(boolean hasElseBranch) {
        this.hasElseBranch = hasElseBranch;
    }

    public void computeElseValues(ContentType contentType, MindustryMetadata metadata, boolean fullBuiltinEvaluation) {
        final LogicLabel label = LogicLabel.EMPTY;
        int count = 0;
        for (int key : targets.keySet()) {
            targetCount.put(key, count++);
        }
        targetCount.put(Integer.MAX_VALUE, count);

        totalSize = computeTotalSize(contentType, metadata);
        elseValues = Math.max(totalSize - targets.size(), 0);

        int firstKey = targets.firstKey();
        int lastKey = targets.lastKey() + 1;
        if (contentType == ContentType.UNKNOWN) {
            leadingSegment = Segment.empty(firstKey, firstKey);
            trailingSegment = Segment.empty(lastKey, lastKey);
        } else {
            boolean limitLow = firstKey > 0;
            boolean limitHigh = !fullBuiltinEvaluation || (lastKey < totalSize);
            leadingSegment = limitLow ? Segment.empty(0, firstKey) : null;
            trailingSegment = limitHigh ? Segment.empty(lastKey, totalSize) : null;
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
        return hasZeroKey;
    }

    public boolean hasNullKey() {
        return hasNullKey;
    }

    public boolean hasNullOrZeroKey() {
        return hasNullKey || hasZeroKey;
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

    /// Returns the number of active targets contained in the given segment
    public int targetCount(Segment segment) {
        return targetCount(segment.from(), segment.to());
    }

    public int targetCount(int from, int to) {
        int fromCount = targetCount.ceilingEntry(from).getValue();
        int toCount = targetCount.ceilingEntry(to).getValue();
        return toCount - fromCount;
    }

    public @Nullable LogicLabel put(@Nullable Integer key, LogicLabel value) {
        if (key == null) {
            LogicLabel previous = nullTarget;
            nullTarget = value;
            hasNullKey = true;
            return previous;
        } else {
            if (key == 0) hasZeroKey = true;
            return targets.put(key, value);
        }
    }

    public void addMovableLabel(LogicLabel label) {
        movableLabels.add(label);
    }

    public boolean isMovableLabel(LogicLabel label) {
        return movableLabels.contains(label);
    }

    public boolean isEmpty() {
        return targets.isEmpty();
    }

    public int size() {
        return targets.size();
    }

    public int range() {
        return targets.isEmpty() ? 0 : targets.lastKey() - targets.firstKey() + 1;
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
