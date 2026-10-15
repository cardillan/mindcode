package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.util.Tuple2;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static java.lang.Math.abs;

@NullMarked
class ConditionOptimizer extends BaseOptimizer {

    ConditionOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.CONDITION_OPTIMIZATION, optimizationContext);
    }

    protected boolean optimizeProgram(OptimizationPhase phase) {
        try (LogicIterator logicIterator = createIterator()) {
            while (logicIterator.hasNext()) {
                if (logicIterator.next() instanceof ConditionalInstruction cond && cond.isConditional() && !cond.getCondition().isStrict()) {
                    if (cond.supportsAlways()) {
                        LogicBoolean result = optimizationContext.evaluatePlainCondition(cond, cond.getX(), cond.getY());
                        if (result == LogicBoolean.TRUE) {
                            logicIterator.set(cond.withAlways());
                            continue;
                        }
                    }

                    Tuple2<LogicValue, LogicValue> condArgs = extractConstantOperand(cond);
                    if (condArgs.e1().isNumericLiteral() && condArgs.e2() instanceof LogicVariable variable
                            && !variable.isGlobalVariable()
                            && findDefiningInstruction(logicIterator, cond, variable) instanceof OpInstruction oper
                            && !oper.isUpdating() && oper.getResult().equals(variable)) {

                        // Try folding first
                        if (!advanced(cond) || !foldConstants(logicIterator, cond, oper, condArgs)) {
                            Operation operation = oper.getOperation();
                            if (operation.isCondition(true) && canInjectCondition(cond, oper)) {
                                // We've already verified we can support strictNotEqual here
                                Condition opCondition = operation.toExistingCondition(true);
                                Condition newCondition = cond.getCondition() == Condition.EQUAL ? opCondition.inverse(true) : opCondition;
                                logicIterator.set(cond.withOperands(newCondition, oper.getX(), oper.getY()));

                                if (getVariableReferences(variable).size() == 1) {
                                    removeInstruction(oper);
                                }
                            } else if (operation == Operation.STRICT_NOT_EQUAL && cond.isPlainComparison()) {
                                // Invert both operation and condition
                                replaceInstruction(oper, oper.withOperands(Operation.STRICT_EQUAL, oper.getX(), oper.getY()));
                                logicIterator.set(cond.invert(getGlobalProfile()));
                            } else if (cond instanceof OpInstruction next){
                                tryMergeOperations(logicIterator, oper, next);
                            }
                        }
                    }
                }
            }
        }

        return wasUpdated();
    }

    private boolean canInjectCondition(ConditionalInstruction cond, OpInstruction oper) {
        Operation operation = oper.getOperation();

        boolean canUseSelect = cond instanceof JumpInstruction
                ? getGlobalProfile().useEmulatedStrictNotEqual()
                : instructionProcessor.isSupported(Opcode.SELECT);

        boolean requireStrictNotEqual = operation.isStrict() &&
                cond.getCondition() == Condition.EQUAL ? operation == Operation.STRICT_EQUAL : operation == Operation.STRICT_NOT_EQUAL;
        if (requireStrictNotEqual && !canUseSelect) return false;

        if (cond instanceof SelectInstruction sx && (sx.getResult().equals(oper.getX()) || sx.getResult().equals(oper.getY()))) return false;

        return cond.isPlainComparison() && operation.isCondition(true);
    }

    private void tryMergeOperations(LogicIterator logicIterator, OpInstruction prev, OpInstruction next) {
        if (next.isPlainComparison()) {
            Operation nextOp = next.getOperation();
            Operation prevOp = prev.getOperation();
            Operation prevOpInverse = prevOp.inverseCondition(false);
            if (prevOp.hasBooleanValue() && (nextOp == Operation.NOT_EQUAL || prevOpInverse != null)) {
                Operation newOperation = nextOp != Operation.NOT_EQUAL ? prevOpInverse : prevOp;
                logicIterator.set(next.withOperands(newOperation, prev.getX(), prev.getY()));

                if (getVariableReferences(prev.getResult()).size() == 1) {
                    removeInstruction(prev);
                }
            }
        }
    }

    private @Nullable LogicInstruction findDefiningInstruction(LogicIterator logicIterator, LogicInstruction cond,
            LogicVariable variable) {
        boolean stale = optimizationContext.isStale(variable);

        if (!stale) {
            LogicInstruction definingInstruction = optimizationContext.findDefiningInstruction(cond, variable);
            if (definingInstruction != null) return definingInstruction;
        }

        // TODO Fix DFO and remove this
        // An alternative way of finding the definition.
        // Not entirely sure why needed. Will be fixed with DFO rewrite.
        List<LogicInstruction> references = getVariableReferences(variable);
        if (references.size() == 2) {
            LogicInstruction other = references.getLast() == cond ? references.getFirst() : references.getLast();
            return instructionIndex(other) < logicIterator.currentIndex() ? other : null;
        }
        return null;
    }

    private boolean canFoldConstants(OpInstruction operation, Condition condition) {
        return switch (operation.getOperation()) {
            case ADD, SUB, MUL, DIV -> true;
            case IDIV -> switch (condition){
                case LESS_THAN, GREATER_THAN, LESS_THAN_EQ, GREATER_THAN_EQ ->
                        operation.getY().isNumericConstant() && operation.getY().isInteger() && operation.getY().getIntValue() > 0;
                default -> false;
            };
            default -> false;
        };
    }

    private boolean foldConstants(LogicIterator logicIterator, ConditionalInstruction cond, OpInstruction oper,
            Tuple2<LogicValue, LogicValue> condArgs) {
        if (!canFoldConstants(oper, cond.getCondition())) return false;

        Tuple2<LogicValue, LogicValue> operArgs = extractConstantOperand(oper);
        if (operArgs.e1().isNumericLiteral() && operArgs.e2() instanceof LogicVariable operVar) {
            // Number is first in a non-commutative condition --> swap
            boolean condSwapped = !cond.getCondition().isCommutative() && condArgs.e1() == cond.getX();
            // Number is first in a non-commutative operation --> swap
            boolean operSwapped = !oper.getOperation().isCommutative() && operArgs.e1() == oper.getX();

            // The condition has a form of x <operation> a <condition> b, where x is a variable and a, b are constants
            double a = operArgs.e1().getDoubleValue();
            double b = condArgs.e1().getDoubleValue();
            double comparison, control;
            double min = abs(b) < abs(a) ? b : a;
            Condition condition = condSwapped ^ operSwapped ? cond.getCondition().opposite() : cond.getCondition();

            switch (oper.getOperation()) {
                case ADD -> {
                    comparison = b - a;
                    control = abs(b) < abs(a) ? comparison + a : b - comparison;
                }
                case MUL -> {
                    comparison = b / a;
                    control = abs(b) < abs(a) ? comparison * a : b / comparison;
                }
                case SUB -> {
                    if (operSwapped) {
                        comparison = a - b;
                        control = abs(b) < abs(a) ? a - comparison : comparison + b;
                    } else {
                        comparison = b + a;
                        control = abs(b) < abs(a) ? comparison - a : comparison - b;
                    }
                }
                case DIV -> {
                    if (operSwapped) {
                        comparison = a / b;
                        control = abs(b) < abs(a) ? a / comparison : comparison * b;
                    } else {
                        comparison = b * a;
                        control = abs(b) < abs(a) ? comparison / a : comparison / b;
                    }
                }
                case IDIV -> {
                    // The condition in general has the form of x \ a < b, for different operators
                    // The condition value must be an integer; can be negative though

                    if (operSwapped ||b != (int) b || a != (int) a || a <= 0 || Math.abs((b + 1) * a) > 1L << 52) return false;
                    Condition c = condSwapped ? cond.getCondition().opposite() : cond.getCondition();

                    if (b >= 0) {
                        switch (c) {
                            case LESS_THAN ->       { condition = Condition.LESS_THAN; comparison = a * b; }
                            case LESS_THAN_EQ ->    { condition = Condition.LESS_THAN; comparison = a * (b + 1); }
                            case GREATER_THAN ->    { condition = Condition.GREATER_THAN_EQ; comparison = a * (b + 1); }
                            case GREATER_THAN_EQ -> { condition = Condition.GREATER_THAN_EQ; comparison = a * b; }
                            default -> throw new IllegalStateException("Unexpected condition.");
                        }
                    } else {
                        switch (c) {
                            case LESS_THAN ->       { condition = Condition.LESS_THAN_EQ; comparison = a * (b + 1); }
                            case LESS_THAN_EQ ->    { condition = Condition.LESS_THAN_EQ; comparison = a * b; }
                            case GREATER_THAN ->    { condition = Condition.GREATER_THAN; comparison = a * b; }
                            case GREATER_THAN_EQ -> { condition = Condition.GREATER_THAN; comparison = a * (b + 1); }
                            default -> throw new IllegalStateException("Unexpected condition.");
                        }
                    }
                    // Can't get a precision error here
                    control = min;
                }
                default -> throw new IllegalStateException();
            }

            // Precision check
            // Note: strict equality can never get here
            double limit = cond.getCondition().isEquality() ? 1e-7 : 0;
            if (abs(control - min) > limit) return false;

            Optional<LogicLiteral> literal = instructionProcessor.createLiteral(SourcePosition.EMPTY,
                    comparison, false);

            if (literal.isPresent()) {
                logicIterator.set(cond.withOperands(condition, operVar, literal.get()));
                if (getVariableReferences(oper.getResult()).size() == 1) {
                    removeInstruction(oper);
                }
                return true;
            }
        }

        return false;
    }
}
