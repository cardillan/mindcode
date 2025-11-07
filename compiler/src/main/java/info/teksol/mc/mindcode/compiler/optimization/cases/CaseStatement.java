package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.mimex.ContentType;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.profile.BuiltinEvaluation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;

@NullMarked
public class CaseStatement {
    private static final int MAX_CASE_RANGE = 1000;

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

    private @Nullable LogicVariable variable = null;
    private int originalCost;
    private int originalSteps;

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

        this.variable = other.variable;
        this.originalCost = other.originalCost;
        this.originalSteps = other.originalSteps;
    }

    public CaseStatement duplicate() {
        return new CaseStatement(this);
    }

    private void computeElseValues(ContentType contentType, MindustryMetadata metadata, boolean fullBuiltinEvaluation) {
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

    public Collection<Branch> getBranches() {
        return branches.values();
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

    private boolean addBranchKeys(int rangeLowValue, int rangeHighValue, LogicLabel target) {
        if (branch == null || !branch.label.equals(target)) {
            branch = new Branch(target);
            branches.put(target, branch);
        }

        for (int i = rangeLowValue; i < rangeHighValue; i++) {
            if (targets.put(i, branch) != null) return false;
        }
        return true;
    }

    private boolean setElseBranch(LogicLabel label) {
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

    private void addMovableLabel(LogicLabel label) {
        movableLabels.add(label);
    }

    public boolean isMovableLabel(LogicLabel label) {
        return movableLabels.contains(label);
    }

    private boolean isEmpty() {
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

    private boolean addBranchInstruction(LogicInstruction ix) {
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

    public LogicVariable getVariable() {
        return Objects.requireNonNull(variable);
    }

    public int getOriginalCost() {
        return originalCost;
    }

    public int getOriginalSteps() {
        return originalSteps;
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

    private enum ExpState {CONDITION, BODY, FLOW}

    public static @Nullable CaseStatement analyze(OptimizationContext optimizationContext, ValueAnalyzer analyzer, AstContext context,
            boolean declaredElseBranch) {
        return new CaseStatementCreator(optimizationContext, analyzer, declaredElseBranch).analyze(context);
    }

    private static class CaseStatementCreator {
        private final OptimizationContext optimizationContext;
        private final ValueAnalyzer analyzer;
        private final boolean declaredElseBranch;

        public CaseStatementCreator(OptimizationContext optimizationContext, ValueAnalyzer analyzer, boolean declaredElseBranch) {
            this.optimizationContext = optimizationContext;
            this.analyzer = analyzer;
            this.declaredElseBranch = declaredElseBranch;
        }

        private @Nullable LogicLabel findNextLabel(AstContext context, LogicIterator iterator, JumpInstruction jump) {
            AstContext jumpAstContext = jump.getAstContext();
            AstContext flowContext = context.nextChild(jumpAstContext);
            while (iterator.hasNext() && iterator.peek(0) instanceof EmptyInstruction noop && noop.belongsTo(jumpAstContext)) {
                // Skipping noops, nothing else may occur here
                iterator.next();
            }
            if (flowContext != null && iterator.peek(0).belongsTo(flowContext)) {
                return optimizationContext.obtainContextLabel(flowContext);
            } else {
                // Unsupported case structure
                return null;
            }
        }

        private @Nullable CaseStatement analyze(AstContext context) {
            List<AstContext> conditionContexts = context.findSubcontexts(CONDITION);
            List<AstContext> bodyContexts = context.findSubcontexts(BODY);
            List<AstContext> elseContexts = context.findSubcontexts(ELSE);
            if (conditionContexts.isEmpty() || bodyContexts.isEmpty() || elseContexts.isEmpty()) return null;

            CaseStatement caseStatement = new CaseStatement(declaredElseBranch);

            ContextMatcher conditionMatcher = new ContextMatcher(conditionContexts);
            ContextMatcher bodyMatcher = new ContextMatcher(bodyContexts);
            ContextMatcher elseMatcher = new ContextMatcher(elseContexts);
            AstContext initContext = context.findSubcontext(INIT);

            // Used to compute the size and execution costs of the case expression
            LogicVariable variable = null;
            int jumps = 0;
            int values = 0;
            int savedSteps = 0;
            ExpState expState = null;
            LogicLabel target = null;
            LogicLabel lastLabel = null;
            Integer rangeLowValue = null;

            // ANALYZE CASE STRUCTURE
            // Gathers all information about the case expression we need
            try (LogicIterator iterator = optimizationContext.createIteratorAtContext(context)) {
                // Skip init context -- can't do anything with it
                if (initContext != null) {
                    while (iterator.hasNext() && iterator.peek(0).belongsTo(initContext)) {
                        iterator.next();
                    }
                }

                while (iterator.hasNext()) {
                    LogicInstruction ix = iterator.next();
                    if (conditionMatcher.matches(ix.getAstContext())) {
                        expState = ExpState.CONDITION;

                        // Ignore these in conditions
                        if (ix instanceof LabelInstruction || ix instanceof EmptyInstruction) continue;

                        if (!(ix instanceof JumpInstruction jump)) return null;
                        jumps++;

                        if (jump.isUnconditional()) {
                            // Unconditional jump is a jump to the next when branch/value
                            // An unfinished range expression: we don't understand the structure of this expression, bail out
                            if (rangeLowValue != null) return null;
                            savedSteps += values;
                        } else {
                            // X needs to be the case variable
                            if (!(jump.getX() instanceof LogicVariable var) || (variable != null && !var.equals(variable)))
                                return null;

                            // Unexpected context: we don't understand the structure of this expression, bail out
                            if (optimizationContext.getLabelInstruction(jump.getTarget()).getAstContext().parent() != context)
                                return null;

                            variable = var;
                            LogicValue value = jump.getY();

                            if (jump.getCondition().isEquality()) {
                                // An unfinished range expression: we don't understand the structure of this expression, bail out
                                // Analyzer refuses our value: bail out
                                if (rangeLowValue != null || !analyzer.inspect(value)) return null;

                                if (jump.getCondition() == Condition.EQUAL || jump.getCondition() == Condition.STRICT_EQUAL) {
                                    target = jump.getTarget();
                                } else {
                                    // NOT_EQUAL might have been created by the jump over jump optimization
                                    target = findNextLabel(context, iterator, jump);
                                    if (target == null) return null;
                                }

                                if (!caseStatement.addBranchKey(analyzer.getLastValue(), target)) return null;

                                savedSteps += values;
                                if (analyzer.getLastValue() != null) values++;  // We do not count nulls
                            } else {
                                // This is an inequality comparison -- can't be unconditional.
                                // Needs an integer variable
                                if (!value.isNumericConstant() || !value.isInteger()) return null;

                                if (rangeLowValue == null) {
                                    // Opening jump: must be LESS_THAN
                                    if (jump.getCondition() != Condition.LESS_THAN) return null;
                                    rangeLowValue = value.getIntValue();
                                } else {
                                    int rangeHighValue;     // Exclusive
                                    if (jump.getCondition() == Condition.LESS_THAN || jump.getCondition() == Condition.LESS_THAN_EQ) {
                                        // Closing jump, in original form.
                                        target = jump.getTarget();
                                        rangeHighValue = value.getIntValue() + (jump.getCondition() == Condition.LESS_THAN_EQ ? 1 : 0);
                                    } else {
                                        // The original jump modified by the jump over jump optimization
                                        target = findNextLabel(context, iterator, jump);
                                        if (target == null) return null;
                                        // When the condition is met, the value doesn't belong to the range
                                        rangeHighValue = value.getIntValue() + (jump.getCondition() == Condition.GREATER_THAN ? 1 : 0);
                                    }

                                    int range = rangeHighValue - rangeLowValue;
                                    if (range > MAX_CASE_RANGE) return null;

                                    if (!analyzer.inspectRange(rangeLowValue, rangeHighValue)) return null;

                                    // Add in all targets
                                    if (!caseStatement.addBranchKeys(rangeLowValue, rangeHighValue, target)) return null;

                                    // Each value comes through these two jumps
                                    values += 2 * range;
                                    savedSteps += values;

                                    // Range has been processed
                                    rangeLowValue = null;
                                }
                            }
                        }
                    } else if (bodyMatcher.matches(ix.getAstContext())) {
                        expState = ExpState.BODY;
                        if (!caseStatement.addBranchInstruction(ix)) return null;
                    } else if (elseMatcher.matches(ix.getAstContext())) {
                        if (lastLabel == null || !caseStatement.setElseBranch(lastLabel)) return null;      // Unexpected structure
                        if (!caseStatement.addBranchInstruction(ix)) return null;
                    } else if (ix.getAstContext().parent() == context && ix.getAstContext().matches(FLOW_CONTROL)) {
                        if (expState == ExpState.BODY) {
                            // First instruction after a body

                            // This may happen with compile-time resolved case expressions
                            if (target == null) return null;

                            // The first flow control after a body:
                            // If the first instruction isn't a jump, can't move this body.
                            expState = ix instanceof JumpInstruction jump && jump.isUnconditional() ? ExpState.FLOW : null;
                        } else if (ix instanceof LabelInstruction labelInstruction) {
                            if (expState == ExpState.FLOW) {
                                caseStatement.addMovableLabel(target);
                            }
                            lastLabel = labelInstruction.getLabel();
                        } else {
                            // Only labels expected here
                            return null;
                        }
                    } else if (!ix.getAstContext().belongsTo(context)) {
                        // End of statement
                        break;
                    } else {
                        // An instruction not accounted for --> unrecognized structure
                        return null;
                    }
                }
            }

            // Unsupported case expressions: no input variable, inconsistent types, no branches or range too large
            if (variable == null || analyzer.getContentType() == null || caseStatement.isEmpty() || caseStatement.range() > MAX_CASE_RANGE) {
                return null;
            }

            caseStatement.computeElseValues(analyzer.getContentType(), optimizationContext.getInstructionProcessor().getMetadata(),
                    optimizationContext.getGlobalProfile().getBuiltinEvaluation() == BuiltinEvaluation.FULL);

            caseStatement.variable = variable;
            caseStatement.originalCost = jumps;
            caseStatement.originalSteps = jumps * values - savedSteps + (declaredElseBranch ? caseStatement.getElseValues() * jumps : 0);

            return caseStatement;
        }
    }
}
