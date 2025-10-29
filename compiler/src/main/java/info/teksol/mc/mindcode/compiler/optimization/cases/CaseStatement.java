package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.SetInstruction;
import info.teksol.mc.mindcode.logic.mimex.ContentType;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class CaseStatement {
    private final boolean declaredElseBranch;
    private final Set<LogicLabel> movableLabels = new HashSet<>();
    private final Map<LogicLabel, Branch> branches = new HashMap<>();
    private final NavigableMap<Integer, Branch> targets = new TreeMap<>();
    private boolean hasZeroKey;

    private @Nullable LogicLabel nullTarget;        // Handles null only
    private @Nullable LogicLabel nullOrElseTarget;  // Handles else or null

    // Active branch and null branch
    private @Nullable Branch branch = null;
    private @Nullable Branch nullBranch = null;
    private @Nullable Branch elseBranch = null;

    /// For a given key, contains the number of targets with values less than the key
    private final NavigableMap<Integer, Integer> targetCount = new TreeMap<>();
    private int totalSize;
    private int elseValues;

    private @Nullable Segment leadingSegment;
    private @Nullable Segment trailingSegment;

    public CaseStatement(boolean declaredElseBranch) {
        this.declaredElseBranch = declaredElseBranch;
    }

    private CaseStatement(CaseStatement other) {
        this.declaredElseBranch = other.declaredElseBranch;
        this.movableLabels.addAll(other.movableLabels);
        this.branches.putAll(other.branches);
        this.targets.putAll(other.targets);
        this.hasZeroKey = other.hasZeroKey;
        this.nullTarget = other.nullTarget;
        this.nullOrElseTarget = other.nullOrElseTarget;
        this.branch = other.branch;
        this.nullBranch = other.nullBranch;
        this.elseBranch = other.elseBranch;
        this.targetCount.putAll(other.targetCount);
        this.totalSize = other.totalSize;
        this.elseValues = other.elseValues;
        this.leadingSegment = other.leadingSegment;
        this.trailingSegment = other.trailingSegment;
    }

    public CaseStatement duplicate() {
        return new CaseStatement(this);
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
        if (!contentType.hasLookup) return targets.size();
        String lookupKeyword = contentType.getLookupKeyword();
        Map<Integer, ?> lookupMap = metadata.getLookupMap(lookupKeyword);
        return lookupMap == null ? targets.size() : lookupMap.size();
    }

    public boolean contains(int value) {
        return value >= targets.firstKey() && value <= targets.lastKey();
    }

    public boolean hasDeclaredElseBranch() {
        return declaredElseBranch;
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
        return nullBranch != null;
    }

    public boolean hasNullOrZeroKey() {
        return hasZeroKey || nullBranch != null;
    }

    public Branch getBranch(Integer key) {
        assert elseBranch != null;
        return targets.getOrDefault(key, elseBranch);
    }

    public @Nullable LogicLabel get(Integer key) {
        Branch branch = targets.get(key);
        return branch == null ? null : branch.label;
    }

    public LogicLabel getExisting(Integer key) {
        return Objects.requireNonNull(targets.get(key)).label;
    }

    public LogicLabel getOrDefault(Integer key, LogicLabel defaultValue) {
        Branch branch = targets.get(key);
        return branch == null ? defaultValue : branch.label;
    }

    /// Returns the number of active targets contained in the given segment
    public int targetCount(Segment segment) {
        return targetCount(segment.from(), segment.to());
    }

    public Collection<Branch> getBranches() {
        return branches.values();
    }

    public int targetCount(int from, int to) {
        int fromCount = targetCount.ceilingEntry(from).getValue();
        int toCount = targetCount.ceilingEntry(to).getValue();
        return toCount - fromCount;
    }

    public boolean addBranchKey(@Nullable Integer key, LogicLabel target) {
        if (branch == null || !branch.label.equals(target)) {
            branch = new Branch(target);
            branches.put(target, branch);
        }

        if (key == null) {
            if (nullBranch != null) return false;
            nullTarget = target;
            nullBranch = branch;
            return true;
        } else {
            if (key == 0) hasZeroKey = true;
            return targets.put(key, branch) == null;
        }
    }

    public boolean addBranchKeys(int rangeLowValue, int rangeHighValue, LogicLabel target) {
        if (branch == null || !branch.label.equals(target)) {
            branch = new Branch(target);
            branches.put(target, branch);
        }

        for (int i = rangeLowValue; i < rangeHighValue; i++) {
            if (targets.put(i, branch) != null) return false;
        }
        return true;
    }

    public boolean setElseBranch(LogicLabel label) {
        if (elseBranch == null) {
            nullOrElseTarget = label;
            branch = elseBranch = new Branch(label);
            branches.put(label, branch);
            return true;
        } else {
            return elseBranch.label.equals(label);
        }
    }

    public Branch getElseBranch() {
        return Objects.requireNonNull(elseBranch);
    }

    public Branch getNullOrElseBranch() {
        return nullBranch == null ? Objects.requireNonNull(elseBranch) : nullBranch;
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

    public SortedMap<Integer, Branch> subMap(int from, int to) {
        return targets.subMap(from, to);
    }

    public @Nullable LogicLabel firstLabel() {
        return targets.isEmpty() ? null : targets.firstEntry().getValue().label;
    }

    public Integer firstKey() {
        return targets.firstKey();
    }

    public Integer lastKey() {
        return targets.lastKey();
    }

    public Set<Map.Entry<Integer, Branch>> entrySet() {
        return targets.entrySet();
    }

    public Set<Integer> keySet() {
        return targets.keySet();
    }

    public @Nullable LogicLabel getNullTarget() {
        return nullTarget;
    }

    public void setNullTarget(LogicLabel nullTarget) {
        this.nullTarget = nullTarget;
    }

    public LogicLabel getNullOrElseTarget() {
        return Objects.requireNonNull(nullOrElseTarget);
    }

    public void setNullOrElseTarget(LogicLabel nullOrElseTarget) {
        this.nullOrElseTarget = nullOrElseTarget;
    }

    public boolean addBranchInstruction(LogicInstruction ix) {
        if (branch == null) return false;

        int size = ix.getRealSize(null);
        branch.addSize(size);

        if (ix instanceof SetInstruction set) {
            branch.addAssignment(set.getResult(), set.getValue());
        } else if (size > 0) {
            branch.resetAssignments();
        }

        return true;
    }

    public int getBranchSize(LogicLabel label) {
        return branches.get(label).size;
    }

    public static class Branch {
        public final LogicLabel label;

        // If the branch assigns a single value to a single variable, it will be recorded here
        private @Nullable LogicVariable assignTarget = null;
        private @Nullable LogicValue assignValue = null;
        private @Nullable Integer integerValue = null;

        private int size;

        public Branch(LogicLabel label) {
            this.label = label;
        }

        private void addAssignment(LogicVariable target, LogicValue value) {
            if (assignTarget == null) {
                assignTarget = target;
                assignValue = value;
            } else if (!assignTarget.equals(target) || !Objects.equals(assignValue, value)) {
                resetAssignments();
            }
        }

        private void resetAssignments() {
            assignTarget = LogicVariable.INVALID;
            assignValue = LogicVoid.VOID;
        }

        private void addSize(int size) {
            this.size += size;
        }

        public LogicVariable getAssignTarget() {
            return assignTarget == null ? LogicVariable.INVALID : assignTarget;
        }

        public LogicValue getAssignValue() {
            return assignValue == null ? LogicVoid.VOID : assignValue;
        }

        public @Nullable Integer getIntegerValue() {
            return integerValue;
        }

        public void setIntegerValue(@Nullable Integer integerValue) {
            this.integerValue = integerValue;
        }
    }
}
