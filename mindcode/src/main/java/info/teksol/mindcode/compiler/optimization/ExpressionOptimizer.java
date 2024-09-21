package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.mimex.NumberedConstants;

import java.util.List;
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
                    case OP       -> processOpInstruction(it, (OpInstruction) ix);
                    case SET      -> processSetInstruction(it, (SetInstruction) ix);
                    case SENSOR   -> processSensorInstruction(it, (SensorInstruction) ix);
                    default -> {}
                }
            }
        }

        return false;
    }

    private void processOpInstruction(LogicIterator logicIterator, OpInstruction ix) {
        if (ix.hasSecondOperand()) {
            final Tuple2<LogicValue, LogicValue> opers = extractConstantOperand(ix);
            if (opers.getT1() instanceof LogicNumber num && num.isInteger()) {
                int value = num.getIntValue();
                switch (ix.getOperation()) {
                    case MUL -> {
                        if (value == 0) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), LogicNumber.get(0)));
                            return;
                        } else if (value == 1) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.getT2()));
                            return;
                        }
                    }
                    case DIV -> {
                        if (value == 1 && opers.getT1() == ix.getY()) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.getT2()));
                            return;
                        }
                    }
                    case ADD, SUB -> {
                        if (value == 0) {
                            logicIterator.set(createSet(ix.getAstContext(), ix.getResult(), opers.getT2()));
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
            } else if (aggressive() && property.getName().equals("id")) {
                int id = NumberedConstants.getId(object.getName());
                if (id != -1) {
                    logicIterator.set(createSet(ix.getAstContext(),ix.getResult(), LogicNumber.get(id)));
                }
            }
        }
    }
}
