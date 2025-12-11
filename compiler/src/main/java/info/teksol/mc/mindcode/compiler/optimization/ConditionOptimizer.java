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
                    Tuple2<LogicValue, LogicValue> condArgs = extractConstantOperand(cond);
                    if (condArgs.e1().isNumericLiteral() && condArgs.e2() instanceof LogicVariable variable
                            && findDefiningInstruction(logicIterator, cond, variable) instanceof OpInstruction oper
                            && !oper.isUpdating() && oper.getResult().equals(variable)) {

                        Operation operation = oper.getOperation();
                        if (advanced(cond) && canFoldConstants(operation)) {
                            foldConstants(logicIterator, cond, oper, condArgs);
                        } else if (operation.isCondition(false) && canInjectCondition(cond, oper)) {
                            // We've already verified we can support strictNotEqual here
                            Condition opCondition = operation.toExistingCondition(true);
                            Condition newCondition = cond.getCondition() == Condition.EQUAL ? opCondition.inverse(true) : opCondition;
                            logicIterator.set(cond.withOperands(newCondition, oper.getX(), oper.getY()));

                            if (getVariableReferences(variable).size() == 1) {
                                removeInstruction(oper);
                            }
                        } else if (cond instanceof OpInstruction next){
                            tryMergeOperations(logicIterator, oper, next);
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean canInjectCondition(ConditionalInstruction cond, OpInstruction oper) {
        Operation operation = oper.getOperation();

        boolean canUseSelect = cond instanceof JumpInstruction ? getGlobalProfile().useEmulatedStrictNotEqual()
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

        // An alternative way of finding the definition.
        // Not entirely sure why needed. Will be fixed with DFO rewrite.
        List<LogicInstruction> references = getVariableReferences(variable);
        if (references.size() == 2) {
            if (stale) {
                LogicInstruction other = references.getLast() == cond ? references.getFirst() : references.getLast();
                return instructionIndex(other) < logicIterator.currentIndex() ? other : null;
            } else {
                // The instruction order is intact
                return references.getLast() == cond ? references.getFirst() : null;
            }
        }
        return null;
    }

    private boolean canFoldConstants(Operation operation) {
        return switch (operation) {
            case ADD, SUB, MUL, DIV -> true;
            default -> false;
        };
    }

    private void foldConstants(LogicIterator logicIterator, ConditionalInstruction cond, OpInstruction oper,
            Tuple2<LogicValue, LogicValue> condArgs) {
        Tuple2<LogicValue, LogicValue> operArgs = extractConstantOperand(oper);
        if (operArgs.e1().isNumericLiteral() && operArgs.e2() instanceof LogicVariable operVar) {
            // Number is first in a non-commutative condition --> swap
            boolean condSwapped = !cond.getCondition().isCommutative() && condArgs.e1() == cond.getX();
            // Number is first in a non-commutative operation --> swap
            boolean operSwapped = !oper.getOperation().isCommutative() && operArgs.e1() == oper.getX();
            double condValue = condArgs.e1().getDoubleValue();
            double operValue = operArgs.e1().getDoubleValue();
            double comparison, control;
            double min = abs(condValue) < abs(operValue) ? condValue : operValue;

            switch (oper.getOperation()) {
                case ADD -> {
                    comparison = condValue - operValue;
                    control = abs(condValue) < abs(operValue) ? comparison + operValue : condValue - comparison;
                }
                case MUL -> {
                    comparison = condValue / operValue;
                    control = abs(condValue) < abs(operValue) ? comparison * operValue : condValue / comparison;
                }
                case SUB -> {
                    if (operSwapped) {
                        comparison = operValue - condValue;
                        control = abs(condValue) < abs(operValue) ? operValue - comparison : comparison + condValue;
                    } else {
                        comparison = condValue + operValue;
                        control = abs(condValue) < abs(operValue) ? comparison - operValue : comparison - condValue;
                    }
                }
                case DIV -> {
                    if (operSwapped) {
                        comparison = operValue / condValue;
                        control = abs(condValue) < abs(operValue) ? operValue / comparison : comparison * condValue;
                    } else {
                        comparison = condValue * operValue;
                        control = abs(condValue) < abs(operValue) ? comparison / operValue : comparison / condValue;
                    }
                }
                default -> throw new IllegalStateException();
            }

            // Precision check
            // Note: strict equality can never get here
            double limit = cond.getCondition().isEquality() ? 1e-7 : 0;
            if (abs(control - min) > limit) return;

            Optional<LogicLiteral> literal = instructionProcessor.createLiteral(SourcePosition.EMPTY,
                    comparison, false);

            if (literal.isPresent()) {
                Condition condition = condSwapped ^ operSwapped ? cond.getCondition().opposite() : cond.getCondition();
                logicIterator.set(cond.withOperands(condition, operVar, literal.get()));
                if (getVariableReferences(oper.getResult()).size() == 1) {
                    removeInstruction(oper);
                }
            }
        }
    }
}
