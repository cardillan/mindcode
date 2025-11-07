package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.evaluator.ExpressionEvaluator;
import info.teksol.mc.evaluator.ExpressionValue;
import info.teksol.mc.evaluator.LogicReadable;
import info.teksol.mc.mindcode.compiler.optimization.DataFlowVariableStates.VariableStates;
import info.teksol.mc.mindcode.compiler.optimization.DataFlowVariableStates.VariableValue;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.ConditionalInstruction;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.OpInstruction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static info.teksol.mc.mindcode.logic.arguments.LogicNull.NULL;
import static info.teksol.mc.mindcode.logic.arguments.Operation.*;

@NullMarked
public class OptimizerExpressionEvaluator {

    private final InstructionProcessor ixProcessor;

    /** Cached single instance for obtaining the results of evaluating expressions. */
    private final ExpressionValue expressionValue;

    OptimizerExpressionEvaluator(InstructionProcessor instructionProcessor) {
        ixProcessor = instructionProcessor;
        expressionValue = new ExpressionValue(instructionProcessor);
    }

    @Nullable LogicLiteral evaluateOpInstruction(OpInstruction op) {
        if (op.hasSecondOperand()) {
            return evaluateBinaryOpInstruction(op);
        } else {
            return evaluateUnaryOpInstruction(op);
        }
    }

    private @Nullable LogicLiteral evaluateUnaryOpInstruction(OpInstruction op) {
        if (op.getX().isConstant()) {
            return evaluate(op.sourcePosition(), op.getOperation(), op.getX(), NULL);
        }
        return null;
    }

    private @Nullable LogicLiteral evaluateBinaryOpInstruction(OpInstruction op) {
        return op.getX().isConstant() && op.getY().isConstant()
                ? evaluate(op.sourcePosition(), op.getOperation(), op.getX(), op.getY())
                : null;
    }

    @Nullable OpInstruction extendedEvaluate(VariableStates variableStates, OpInstruction op1) {
        LogicLiteral x1 = op1.getX() instanceof LogicLiteral l && l.isNumericLiteral() ? l : null;
        LogicLiteral y1 = op1.getY() instanceof LogicLiteral l && l.isNumericLiteral() ? l : null;
        // We need just one literal
        if (x1 == null ^ y1 == null) {
            LogicLiteral literal1 = x1 == null ? y1 : x1;
            VariableValue variableValue = variableStates.findVariableValue(x1 == null ? op1.getX() : op1.getY());
            if (variableValue != null && variableValue.isExpression()
                    && variableValue.getInstruction() instanceof OpInstruction op2 && op2.hasSecondOperand()) {
                LogicLiteral x2 = op2.getX() instanceof LogicLiteral l && l.isNumericLiteral() ? l : null;
                LogicLiteral y2 = op2.getY() instanceof LogicLiteral l && l.isNumericLiteral() ? l : null;

                if (x2 == null ^ y2 == null) {
                    LogicLiteral literal2 = x2 == null ? y2 : x2;
                    LogicValue value = x2 == null ? op2.getX() : op2.getY();
                    if (value instanceof LogicVariable variable) {
                        if (op1.getOperation() == op2.getOperation() && op1.getOperation().isAssociative()) {
                            // Perform the operation on the two literals
                            LogicLiteral literal = evaluate(op1.sourcePosition(), op1.getOperation(), literal1, literal2);
                            if (literal != null) {
                                // Construct the instruction
                                return ixProcessor.createOp(op1.getAstContext(), op1.getOperation(), op1.getResult(), variable, literal);
                            }
                        } else if (op1.getOperation() == ADD && op2.getOperation() == SUB) {
                            return evaluateAddAfterSub(op1, ADD, SUB, x2 != null, literal1, literal2, variable);
                        } else if (op1.getOperation() == SUB && op2.getOperation() == ADD) {
                            return evaluateSubAfterAdd(op1, ADD, SUB, x1 != null, literal1, literal2, variable);
                        } else if (op1.getOperation() == SUB && op2.getOperation() == SUB) {
                            return evaluateSubAfterSub(op1, ADD, SUB, x1 != null, x2 != null, literal1, literal2, variable);
                        } else if (op1.getOperation() == MUL && op2.getOperation() == DIV) {
                            return evaluateAddAfterSub(op1, MUL, DIV, x2 != null, literal1, literal2, variable);
                        } else if (op1.getOperation() == DIV && op2.getOperation() == MUL) {
                            return evaluateSubAfterAdd(op1, MUL, DIV, x1 != null, literal1, literal2, variable);
                        } else if (op1.getOperation() == DIV && op2.getOperation() == DIV) {
                            return evaluateSubAfterSub(op1, MUL, DIV, x1 != null, x2 != null, literal1, literal2, variable);
                        }
                    }
                }
            }
        }
        return null;
    }

    private @Nullable OpInstruction evaluateAddAfterSub(OpInstruction op, Operation add, Operation sub,
            boolean literal2first, LogicLiteral literal1, LogicLiteral literal2, LogicVariable variable) {
        if (literal2first) {
            LogicLiteral literal = evaluate(op.sourcePosition(), add, literal2, literal1);
            return literal == null || literal.isNull() ? null
                    : ixProcessor.createOp(op.getAstContext(), sub, op.getResult(), literal, variable);
        } else {
            LogicLiteral literal = evaluate(op.sourcePosition(), sub, literal2, literal1);
            return literal == null || literal.isNull() ? null
                    : ixProcessor.createOp(op.getAstContext(), sub, op.getResult(), variable, literal);
        }
    }

    private @Nullable OpInstruction evaluateSubAfterAdd(OpInstruction op, Operation add, Operation sub,
            boolean literal1first, LogicLiteral literal1, LogicLiteral literal2, LogicVariable variable) {
        LogicLiteral literal = evaluate(op.sourcePosition(), sub, literal1, literal2);
        if (literal != null && !literal.isNull()) {
            return literal1first
                    ? ixProcessor.createOp(op.getAstContext(), sub, op.getResult(), literal, variable)
                    : ixProcessor.createOp(op.getAstContext(), sub, op.getResult(), variable, literal);
        }
        return null;
    }

    private @Nullable OpInstruction evaluateSubAfterSub(OpInstruction op, Operation add, Operation sub,
            boolean literal1first, boolean literal2first, LogicLiteral literal1, LogicLiteral literal2, LogicVariable variable) {
        LogicLiteral literal = literal2first
                ? evaluate(op.sourcePosition(), sub, literal2, literal1)
                : evaluate(op.sourcePosition(), add, literal2, literal1);

        if (literal != null && !literal.isNull()) {
            return literal1first == literal2first
                    ? ixProcessor.createOp(op.getAstContext(), sub, op.getResult(), variable, literal)
                    : ixProcessor.createOp(op.getAstContext(), sub, op.getResult(), literal, variable);
        }
        return null;
    }

    OpInstruction normalize(OpInstruction op) {
        switch (op.getOperation()) {
            case SUB:
                // TODO use isNumericConstant, adapt negation to handle LogicReadable
                if (op.getY() instanceof LogicNumber l && l.getDoubleValue() < 0.0) {
                    return ixProcessor.createOp(op.getAstContext(), ADD, op.getResult(), op.getX(), l.negation(ixProcessor));
                }
                break;

            case DIV:
                // TODO use isNumericConstant, adapt reciprocal to handle LogicReadable
                if (op.getY() instanceof LogicNumber l && l.getDoubleValue() != (long) l.getDoubleValue()) {
                    LogicLiteral reciprocal = l.reciprocal(ixProcessor);
                    if (reciprocal != null && reciprocal.toMlog().length() < l.toMlog().length()) {
                        return ixProcessor.createOp(op.getAstContext(), MUL, op.getResult(), op.getX(), reciprocal);
                    }
                }
                break;

            case MUL:
                // TODO use isNumericConstant, adapt reciprocal to handle LogicReadable
                if (op.getX() instanceof LogicNumber number && op.getY() instanceof LogicVariable variable) {
                    return normalizeMul(op, variable, number);
                } else if (op.getY() instanceof LogicNumber number && op.getX() instanceof LogicVariable variable) {
                    return normalizeMul(op, variable, number);
                }
                break;
        }

        return op;
    }

    OpInstruction normalizeMul(OpInstruction op, LogicVariable variable, LogicNumber number) {
        if (number.getDoubleValue() != (long) number.getDoubleValue()) {
            LogicLiteral reciprocal = number.reciprocal(ixProcessor);
            if (reciprocal != null && reciprocal.toMlog().length() < number.toMlog().length()) {
                return ixProcessor.createOp(op.getAstContext(), DIV, op.getResult(), variable, reciprocal);
            }
        }
        return op;
    }

    public @Nullable LogicLiteral evaluate(SourcePosition sourcePosition, Operation operation, LogicReadable a, LogicReadable b) {
        ExpressionEvaluator.existingOperation(operation).execute(expressionValue, a, b);
        return expressionValue.getLiteral(sourcePosition);
    }

    public @Nullable LogicBoolean evaluateConditionalInstruction(ConditionalInstruction instruction) {
        if (instruction.isUnconditional()) {
            return LogicBoolean.TRUE;
        } else if (instruction.getX().isConstant() && instruction.getY().isConstant()) {
            LogicLiteral literal = evaluate(instruction.sourcePosition(), instruction.getCondition().toOperation(), instruction.getX(), instruction.getY());
            return literal instanceof LogicBoolean b ? b : null;
        }
        return null;
    }
}
