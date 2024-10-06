package info.teksol.mindcode.compiler.optimization;

import info.teksol.evaluator.ExpressionEvaluator;
import info.teksol.evaluator.LogicReadable;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.compiler.optimization.DataFlowVariableStates.VariableStates;
import info.teksol.mindcode.compiler.optimization.DataFlowVariableStates.VariableValue;
import info.teksol.mindcode.logic.*;

import static info.teksol.mindcode.logic.Operation.*;

class OptimizerExpressionEvaluator {

    private final InstructionProcessor ixProcessor;

    /** Cached single instance for obtaining the results of evaluating expressions. */
    private final ExpressionValue expressionValue;

    OptimizerExpressionEvaluator(InstructionProcessor instructionProcessor) {
        ixProcessor = instructionProcessor;
        expressionValue = new ExpressionValue(instructionProcessor);
    }

    LogicLiteral evaluateOpInstruction(OpInstruction op) {
        if (op.hasSecondOperand()) {
            return evaluateBinaryOpInstruction(op);
        } else {
            return evaluateUnaryOpInstruction(op);
        }
    }

    private LogicLiteral evaluateUnaryOpInstruction(OpInstruction op) {
        if (op.getX() instanceof LogicLiteral x && x.isNumericLiteral()) {
            return evaluate(op.getOperation(), x, LogicNull.NULL);
        }
        return null;
    }

    private LogicLiteral evaluateBinaryOpInstruction(OpInstruction op) {
        if (op.getX() instanceof LogicLiteral x && x.isNumericLiteral()
                && op.getY() instanceof LogicLiteral y && y.isNumericLiteral()) {
            return evaluate(op.getOperation(), x, y);
        }
        return null;
    }

    OpInstruction extendedEvaluate(VariableStates variableStates, OpInstruction op1) {
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
                            LogicLiteral literal = evaluate(op1.getOperation(), literal1, literal2);
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

    private OpInstruction evaluateAddAfterSub(OpInstruction op, Operation add, Operation sub,
            boolean literal2first, LogicLiteral literal1, LogicLiteral literal2, LogicVariable variable) {
        if (literal2first) {
            LogicLiteral literal = evaluate(add, literal2, literal1);
            return literal == null || literal.isNull() ? null
                    : ixProcessor.createOp(op.getAstContext(), sub, op.getResult(), literal, variable);
        } else {
            LogicLiteral literal = evaluate(sub, literal2, literal1);
            return literal == null ? null
                    : ixProcessor.createOp(op.getAstContext(), sub, op.getResult(), variable, literal);
        }
    }

    private OpInstruction evaluateSubAfterAdd(OpInstruction op, Operation add, Operation sub,
            boolean literal1first, LogicLiteral literal1, LogicLiteral literal2, LogicVariable variable) {
        LogicLiteral literal = evaluate(sub, literal1, literal2);
        if (literal != null && !literal.isNull()) {
            return literal1first
                    ? ixProcessor.createOp(op.getAstContext(), sub, op.getResult(), literal, variable)
                    : ixProcessor.createOp(op.getAstContext(), sub, op.getResult(), variable, literal);
        }
        return null;
    }

    private OpInstruction evaluateSubAfterSub(OpInstruction op, Operation add, Operation sub,
            boolean literal1first, boolean literal2first, LogicLiteral literal1, LogicLiteral literal2, LogicVariable variable) {
        LogicLiteral literal = literal2first
                ? evaluate(sub, literal2, literal1)
                : evaluate(add, literal2, literal1);

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
                if (op.getY() instanceof LogicNumber l && l.getDoubleValue() < 0.0) {
                    return ixProcessor.createOp(op.getAstContext(), ADD, op.getResult(), op.getX(), l.negation());
                }
                break;

            case DIV:
                if (op.getY() instanceof LogicNumber l && l.getDoubleValue() != (long) l.getDoubleValue()) {
                    LogicNumber reciprocal = l.reciprocal(ixProcessor);
                    if (reciprocal.toMlog().length() < l.toMlog().length()) {
                        return ixProcessor.createOp(op.getAstContext(), MUL, op.getResult(), op.getX(), reciprocal);
                    }
                }
                break;

            case MUL:
                if (op.getX() instanceof LogicNumber number && op.getY() instanceof  LogicVariable variable) {
                    return normalizeMul(op, variable, number);
                } else if (op.getY() instanceof LogicNumber number && op.getX() instanceof  LogicVariable variable) {
                    return normalizeMul(op, variable, number);
                }
                break;
        }

        return op;
    }

    OpInstruction normalizeMul(OpInstruction op, LogicVariable variable, LogicNumber number) {
        if (number.getDoubleValue() != (long) number.getDoubleValue()) {
            LogicNumber reciprocal = number.reciprocal(ixProcessor);
            if (reciprocal.toMlog().length() < number.toMlog().length()) {
                return ixProcessor.createOp(op.getAstContext(), DIV, op.getResult(), variable, reciprocal);
            }
        }
        return op;
    }

    public LogicLiteral evaluate(Operation operation, LogicReadable a, LogicReadable b) {
        ExpressionEvaluator.getOperation(operation).execute(expressionValue, a, b);
        return expressionValue.getLiteral();
    }

    public LogicBoolean evaluateJumpInstruction(JumpInstruction jump) {
        if (jump.isUnconditional()) {
            return LogicBoolean.TRUE;
        } else if (jump.getX() instanceof LogicLiteral x && jump.getY() instanceof LogicLiteral y) {
            LogicLiteral literal = evaluate(jump.getCondition().toOperation(), x, y);
            return literal instanceof LogicBoolean b ? b : null;
        }
        return null;
    }
}
