package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.evaluator.Color;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.mimex.MindustryContents;
import info.teksol.mc.util.Tuple2;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static info.teksol.mc.evaluator.ExpressionEvaluator.clamp01;

/**
 * This optimizer improves and streamlines expressions.
 * <ul><li>
 * Mul/div + floor optimization:
 *      <pre>{@code
 *      op mul __tmp variable1 constant
 *      op floor variable2 __tmp
 *      }</pre>
 * is replaced by
 *      <pre>{@code
 *      op idiv variable2 variable1 (1 / constant)
 *      }</pre>
 * where 1 / constant is evaluated here. If the instruction is div instead of mul, the inverse isn't taken.
 * </li><li>
 * All instructions setting the variable to itself (e.g. `set x x`) are removed.
 * </li></ul>
 */
class ExpressionOptimizer extends BaseOptimizer {
    public ExpressionOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.EXPRESSION_OPTIMIZATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        try (LogicIterator it = createIterator()) {
            while (it.hasNext()) {
                switch (it.next()) {
                    case LookupInstruction ix     -> processLookupInstruction(it, ix);
                    case OpInstruction ix         -> processOpInstruction(it, ix);
                    case PackColorInstruction ix  -> processPackColorInstruction(it, ix);
                    case SetInstruction ix        -> processSetInstruction(it, ix);
                    case SensorInstruction ix     -> processSensorInstruction(it, ix);
                    default -> {}
                }
            }
        }

        return false;
    }

    private void processLookupInstruction(LogicIterator logicIterator, LookupInstruction ix) {
        if (advanced() && ix.getIndex() instanceof LogicNumber number) {
            Map<Integer, ? extends MindustryContent> lookupMap = MindustryContents.getLookupMap(ix.getType().getKeyword());
            if (lookupMap != null) {
                MindustryContent object = lookupMap.get(number.getIntValue());
                if (object != null) {
                    logicIterator.set(createSet(ix.getAstContext(),ix.getResult(), LogicBuiltIn.create(object.name())));
                }
            }
        }
    }

    private void processOpInstruction(LogicIterator logicIterator, OpInstruction ix) {
        if (ix.hasSecondOperand()) {
            final Tuple2<LogicValue, LogicValue> opers = extractConstantOperand(ix);
            if (opers.e1() instanceof LogicNumber num && num.isInteger()) {
                long value = num.getLongValue();
                switch (ix.getOperation()) {
                    case MUL -> {
                        if (value == 0) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNumber.ZERO));
                            return;
                        } else if (value == 1) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.e2()));
                            return;
                        }
                    }
                    case DIV -> {
                        if (value == 0 && opers.e1() == ix.getY()) {
                            // Division by zero
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNull.NULL));
                            return;
                        } else if (advanced() && value == 0 && opers.e1() == ix.getX()) {
                            // Zero divided by something
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNumber.ZERO));
                            return;
                        } else if (value == 1 && opers.e1() == ix.getY()) {
                            // Division by one
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.e2()));
                            return;
                        }
                    }
                    case IDIV, MOD -> {
                        if (value == 0 && opers.e1() == ix.getY()) {
                            // Division by zero
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNull.NULL));
                            return;
                        } else if (advanced() && value == 0 && opers.e1() == ix.getX()) {
                            // Zero divided by something
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNumber.ZERO));
                            return;
                        }
                    }
                    case SUB -> {
                        if (value == 0 && opers.e1() == ix.getY()) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.e2()));
                            return;
                        }
                    }
                    case ADD, BITWISE_XOR -> {
                        if (value == 0 && (advanced() || ix.getOperation() != Operation.BITWISE_XOR)) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.e2()));
                            return;
                        }
                    }
                    case SHL, SHR -> {
                        if (advanced() && (value == 0 && opers.e1() == ix.getY())) {
                            // Shift by zero
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), ix.getX()));
                            return;
                        } else if (value == 0 && opers.e1() == ix.getX()) {
                            // Shifting zero by something
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNumber.ZERO));
                            return;
                        }
                    }
                    case BITWISE_OR, BOOLEAN_OR, LOGICAL_OR -> {
                        if (advanced() && value == 0 && ix.getOperation() != Operation.BOOLEAN_OR) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.e2()));
                            return;
                        } else if (value != 0 && ix.getOperation() != Operation.BITWISE_OR) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicBoolean.TRUE));
                            return;
                        }
                    }
                    case BITWISE_AND, BOOLEAN_AND, LOGICAL_AND -> {
                        if (value == 0) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicBoolean.FALSE));
                            return;
                        } else if (advanced() && ix.getOperation() == Operation.LOGICAL_AND) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.e2()));
                        }
                    }
                }
            } else {
                DataFlowVariableStates.VariableStates variableStates = optimizationContext.getVariableStates(ix);
                LogicValue x = optimizationContext.resolveValue(variableStates, ix.getX());
                LogicValue y = optimizationContext.resolveValue(variableStates, ix.getY());
                if (x.equals(y)) {
                    // Both operands are the same variable
                    switch (ix.getOperation()) {
                        case EQUAL, LESS_THAN_EQ, GREATER_THAN_EQ, STRICT_EQUAL -> {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicBoolean.TRUE));
                            return;
                        }
                        case NOT_EQUAL, LESS_THAN, GREATER_THAN -> {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicBoolean.FALSE));
                            return;
                        }
                        case BITWISE_AND, BITWISE_OR, LOGICAL_AND, LOGICAL_OR, MIN, MAX -> {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), ix.getX()));
                            return;
                        }
                        case SUB, BITWISE_XOR -> {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNumber.ZERO));
                            return;
                        }
                    }
                }
            }
        }

        final Tuple2<LogicValue, LogicValue> ops = extractIdivOperands(ix);
        if (ops != null) {
            LogicVariable result = ix.getResult();
            List<LogicInstruction> list = instructions(in -> in.getArgs().contains(result) && !(in instanceof PushOrPopInstruction));

            // Preconditions:
            // - exactly two instructions
            // - div/idiv/mul comes first
            // - the second is the floor operation
            // - the second operates on the result of the first
            if (list.size() == 2 && list.get(0) == ix && list.get(1) instanceof OpInstruction ox
                    && ox.getOperation() == Operation.FLOOR && ox.getX().equals(ix.getResult())) {

                replaceInstruction(ox, createOp(ox.getAstContext(),
                        Operation.IDIV, ox.getResult(), ops.e1(), ops.e2()));
                logicIterator.set(createNoOp(ix.getAstContext()));
                logicIterator.next();
            }
        }
    }

    /**
     * Returns the operands in a tuple. If one of the operands is a numeric literal, it will be returned
     * in e1 (the operands may get swapped).

     * @param ix instruction to inspect
     * @return a tuple containing a constant operand and the other operand.
     */
    private Tuple2<LogicValue, LogicValue> extractConstantOperand(OpInstruction ix) {
        return ix.getX().isNumericLiteral()
                ? Tuple2.ofSame(ix.getX(), ix.getY())
                : Tuple2.ofSame(ix.getY(), ix.getX());
    }

    private Tuple2<LogicValue, LogicValue> extractIdivOperands(OpInstruction ix) {
        if (!ix.getResult().isTemporaryVariable()) {
            return null;
        } else {
            return switch (ix.getOperation()) {
                case DIV, IDIV -> Tuple2.ofSame(ix.getX(), ix.getY());

                case MUL ->
                        ix.getX().isNumericLiteral() ? invertMultiplicand(ix.getY(), ix.getX()) :
                        ix.getY().isNumericLiteral() ? invertMultiplicand(ix.getX(), ix.getY()) :
                        null;

                default -> null;
            };
        }
    }

    private Tuple2<LogicValue, LogicValue> invertMultiplicand(LogicValue variable, LogicValue literal) {
        // We know literal is a NumericLiteral
        double multiplicand = ((LogicLiteral) literal).getDoubleValue();
        double divisor = 1.0d / multiplicand;
        Optional<String> inverted = instructionProcessor.mlogFormat(divisor);
        return inverted.map(lit -> Tuple2.ofSame(variable, LogicNumber.get(lit, divisor))).orElse(null);
    }

    private void processPackColorInstruction(LogicIterator logicIterator, PackColorInstruction ix) {
        if (ix.inputArgumentsStream().allMatch(LogicArgument::isNumericLiteral)) {
            float r = clamp01(((LogicLiteral) ix.getR()).getDoubleValue());
            float g = clamp01(((LogicLiteral) ix.getG()).getDoubleValue());
            float b = clamp01(((LogicLiteral) ix.getB()).getDoubleValue());
            float a = clamp01(((LogicLiteral) ix.getA()).getDoubleValue());
            String literal = Color.toColorLiteral(r, g, b, a);
            LogicColor color = LogicColor.create(
                    Color.toDoubleBits(r, g, b, a),
                    Color.toColorLiteral(r, g, b, a));
            logicIterator.set(createSet(ix.getAstContext(),ix.getResult(), color));
        }
    }

    private void processSetInstruction(LogicIterator logicIterator, SetInstruction ix) {
        if (ix.getResult().equals(ix.getValue())) {
            logicIterator.set(createNoOp(ix.getAstContext()));
            logicIterator.next();
        }
    }

    private void processSensorInstruction(LogicIterator logicIterator, SensorInstruction ix) {
        if (ix.getObject() instanceof LogicBuiltIn object && ix.getProperty() instanceof LogicBuiltIn property) {
            if (object.getName().equals("@this")) {
                if (property.getName().equals("@x") || property.getName().equals("@y")) {
                    logicIterator.set(createSet(ix.getAstContext(),ix.getResult(),
                            LogicBuiltIn.create(object.getName() + property.getName().substring(1))));
                }
            } else if (advanced() && property.getName().equals("@id") &&  object.getObject().id() != -1) {
                logicIterator.set(createSet(ix.getAstContext(),ix.getResult(), LogicNumber.get(object.getObject().id())));
            }
        }
    }
}