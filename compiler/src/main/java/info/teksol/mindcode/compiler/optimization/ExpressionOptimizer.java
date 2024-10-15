package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.mimex.MindustryContent;
import info.teksol.mindcode.mimex.MindustryContents;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        // Cannot use for-each due to modifications of the underlying list in the loop
        try (LogicIterator it = createIterator()) {
            while (it.hasNext()) {
                LogicInstruction ix = it.next();
                switch (ix.getOpcode()) {
                    case LOOKUP   -> processLookupInstruction(it, (LookupInstruction) ix);
                    case OP       -> processOpInstruction(it, (OpInstruction) ix);
                    case SET      -> processSetInstruction(it, (SetInstruction) ix);
                    case SENSOR   -> processSensorInstruction(it, (SensorInstruction) ix);
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
            if (opers.getT1() instanceof LogicNumber num && num.isInteger()) {
                int value = num.getIntValue();
                switch (ix.getOperation()) {
                    case MUL -> {
                        if (value == 0) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNumber.ZERO));
                            return;
                        } else if (value == 1) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.getT2()));
                            return;
                        }
                    }
                    case DIV -> {
                        if (value == 0 && opers.getT1() == ix.getY()) {
                            // Division by zero
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNull.NULL));
                            return;
                        } else if (value == 0 && opers.getT1() == ix.getX()) {
                            // Zero divided by something
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNumber.ZERO));
                            return;
                        } else if (value == 1 && opers.getT1() == ix.getY()) {
                            // Division by one
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.getT2()));
                            return;
                        }
                    }
                    case IDIV, MOD -> {
                        if (value == 0 && opers.getT1() == ix.getY()) {
                            // Division by zero
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNull.NULL));
                            return;
                        } else if (value == 0 && opers.getT1() == ix.getX()) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNumber.ZERO));
                            return;
                        }
                    }
                    case ADD, SUB, BINARY_OR, LOGICAL_OR, XOR, SHL, SHR -> {
                        if (value == 0) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.getT2()));
                            return;
                        }
                    }
                    case BINARY_AND, BOOL_AND, LOGICAL_AND -> {
                        if (value == 0) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNumber.ZERO));
                            return;
                        } else if (ix.getOperation() == Operation.LOGICAL_AND) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.getT2()));
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
                        case BINARY_AND, BINARY_OR, LOGICAL_AND, LOGICAL_OR, MIN, MAX -> {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), ix.getX()));
                            return;
                        }
                        case SUB, XOR -> {
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
                        Operation.IDIV, ox.getResult(), ops.getT1(), ops.getT2()));
                logicIterator.set(createNoOp(ix.getAstContext()));
                logicIterator.next();
            }
        }
    }

    /**
     * Returns the operands in a tuple. If one of the operands is a numeric literal, it will be returned
     * in _1 (the operands may get swapped).

     * @param ix instruction to inspect
     * @return a tuple containing a constant operand and the other operand.
     */
    private Tuple2<LogicValue, LogicValue> extractConstantOperand(OpInstruction ix) {
        return ix.getX().isNumericLiteral()
                ? new Tuple2<>(ix.getX(), ix.getY())
                : new Tuple2<>(ix.getY(), ix.getX());
    }

    private Tuple2<LogicValue, LogicValue> extractIdivOperands(OpInstruction ix) {
        if (!ix.getResult().isTemporaryVariable()) {
            return null;
        } else {
            return switch (ix.getOperation()) {
                case DIV, IDIV -> new Tuple2<>(ix.getX(), ix.getY());

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

    private void processSetInstruction(LogicIterator logicIterator, SetInstruction ix) {
        if (ix.getResult().equals(ix.getValue())) {
            logicIterator.set(createNoOp(ix.getAstContext()));
            logicIterator.next();
        }
    }

    private void processSensorInstruction(LogicIterator logicIterator, SensorInstruction ix) {
        if (ix.getObject() instanceof LogicBuiltIn object && object.isConstant()
                && ix.getProperty() instanceof LogicBuiltIn property) {
            if (object.getName().equals("this")) {
                if (property.getName().equals("x") || property.getName().equals("y")) {
                    logicIterator.set(createSet(ix.getAstContext(),ix.getResult(),
                            LogicBuiltIn.create(object.getName() + property.getName())));
                }
            } else if (advanced() && property.getName().equals("id")) {
                int id = MindustryContents.getId(object.getName());
                if (id != -1) {
                    logicIterator.set(createSet(ix.getAstContext(),ix.getResult(), LogicNumber.get(id)));
                }
            }
        }
    }
}
