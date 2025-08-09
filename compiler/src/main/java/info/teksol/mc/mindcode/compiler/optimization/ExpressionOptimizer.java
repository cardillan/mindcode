package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.evaluator.Color;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.generation.variables.ArrayStore;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.profile.BuiltinEvaluation;
import info.teksol.mc.util.Tuple2;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static info.teksol.mc.evaluator.ExpressionEvaluator.clamp01;

/// This optimizer improves and streamlines expressions.
///   - Mul/div + floor optimization:
///     <br>
///     `op mul __tmp variable1 constant; op floor variable2 __tmp`<br>
///     is replaced by
///     <br>
///     `op idiv variable2 variable1 (1 / constant)`<br>
///     where 1 / constant is evaluated here. If the instruction is div instead of mul, the inverse isn't taken.
///   - All instructions setting the variable to itself (e.g. `set x x`) are removed.
@NullMarked
class ExpressionOptimizer extends BaseOptimizer {

    private final OptimizerExpressionEvaluator expressionEvaluator;

    public ExpressionOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.EXPRESSION_OPTIMIZATION, optimizationContext);
        expressionEvaluator = new OptimizerExpressionEvaluator(instructionProcessor);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        try (LogicIterator it = createIterator()) {
            while (it.hasNext()) {
                switch (it.next()) {
                    case LookupInstruction ix     -> processLookupInstruction(it, ix);
                    case OpInstruction ix         -> processOpInstruction(it, ix);
                    case PackColorInstruction ix  -> processPackColorInstruction(it, ix);
                    case SelectInstruction ix     -> processSelectInstruction(it, ix);
                    case SensorInstruction ix     -> processSensorInstruction(it, ix);
                    case SetInstruction ix        -> processSetInstruction(it, ix);
                    case ReadInstruction ix       -> processReadInstruction(it, ix);
                    case WriteInstruction ix      -> processWriteInstruction(it, ix);
                    case ReadArrInstruction ix    -> processReadArrInstruction(it, ix);
                    case WriteArrInstruction ix   -> processWriteArrInstruction(it, ix);
                    default -> {}
                }
            }
        }

        return false;
    }

    private void processLookupInstruction(LogicIterator logicIterator, LookupInstruction ix) {
        if (getProfile().getBuiltinEvaluation() != BuiltinEvaluation.NONE && ix.getIndex().isNumericConstant() && ix.getIndex().isInteger()) {
            Map<Integer, ? extends MindustryContent> lookupMap = metadata.getLookupMap(ix.getType().getKeyword());
            if (lookupMap != null) {
                MindustryContent object = lookupMap.get(ix.getIndex().getIntValue());
                if (object != null && !object.legacy() && (getProfile().getBuiltinEvaluation() == BuiltinEvaluation.FULL || metadata.isStableBuiltin(object.name()))) {
                    logicIterator.set(createSet(ix.getAstContext(),ix.getResult(), LogicBuiltIn.create(object, false)));
                }
            }
        }
    }

    private void processOpInstruction(LogicIterator logicIterator, OpInstruction ix) {
        if (ix.hasSecondOperand()) {
            final Tuple2<LogicValue, LogicValue> opers = extractConstantOperand(ix);
            if (opers.e1().isNumericConstant() && opers.e1().isLong()) {
                long value = opers.e1().getLongValue();
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
                logicIterator.set(createEmpty(ix.getAstContext()));
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

    private @Nullable Tuple2<LogicValue, LogicValue> extractIdivOperands(OpInstruction ix) {
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

    private @Nullable Tuple2<LogicValue, LogicValue> invertMultiplicand(LogicValue variable, LogicValue literal) {
        // We know literal is a NumericLiteral
        double multiplicand = literal.getDoubleValue();
        double divisor = 1.0d / multiplicand;
        Optional<LogicLiteral> inverted = instructionProcessor.createLiteral(SourcePosition.EMPTY, divisor, false);
        return inverted.map(lit -> Tuple2.ofSame(variable, lit)).orElse(null);
    }

    private void processPackColorInstruction(LogicIterator logicIterator, PackColorInstruction ix) {
        if (ix.inputArgumentsStream().allMatch(LogicArgument::isNumericLiteral)) {
            float r = clamp01(ix.getR().getDoubleValue());
            float g = clamp01(ix.getG().getDoubleValue());
            float b = clamp01(ix.getB().getDoubleValue());
            float a = clamp01(ix.getA().getDoubleValue());
            LogicColor color = LogicColor.create(SourcePosition.EMPTY,
                    Color.toDoubleBitsClamped(r, g, b, a),
                    Color.toColorLiteralClamped(r, g, b, a));
            logicIterator.set(createSet(ix.getAstContext(),ix.getResult(), color));
        }
    }

    private void processSelectInstruction(LogicIterator logicIterator, SelectInstruction ix) {
        LogicBoolean condition = expressionEvaluator.evaluateConditionalInstruction(ix);
        switch(condition) {
            case null -> {}
            case TRUE -> logicIterator.set(createSet(ix.getAstContext(),ix.getResult(), ix.getTrueValue()));
            case FALSE -> logicIterator.set(createSet(ix.getAstContext(),ix.getResult(), ix.getFalseValue()));
        }
    }

    private void processSensorInstruction(LogicIterator logicIterator, SensorInstruction ix) {
        if (ix.getObject() instanceof LogicBuiltIn object && ix.getProperty() instanceof LogicBuiltIn property) {
            if (object.equals(LogicBuiltIn.THIS)) {
                if (property.equals(LogicBuiltIn.X)) {
                    logicIterator.set(createSet(ix.getAstContext(),ix.getResult(), LogicBuiltIn.THIS_X));
                } else if (property.equals(LogicBuiltIn.Y)) {
                    logicIterator.set(createSet(ix.getAstContext(),ix.getResult(), LogicBuiltIn.THIS_Y));
                }
            } else if (getProfile().getBuiltinEvaluation() != BuiltinEvaluation.NONE && property.equals(LogicBuiltIn.ID)
                    && object.getObject() != null && object.getObject().logicId() != -1 && !object.getObject().legacy()) {
                if (getProfile().getBuiltinEvaluation() == BuiltinEvaluation.FULL || metadata.isStableBuiltin(object.getObject().name())) {
                    logicIterator.set(createSet(ix.getAstContext(), ix.getResult(),
                            LogicNumber.create(ix.sourcePosition(), object.getObject().logicId())));
                }
            }
        }
    }

    private void processSetInstruction(LogicIterator logicIterator, SetInstruction ix) {
        if (ix.getResult().equals(ix.getValue())) {
            logicIterator.set(createEmpty(ix.getAstContext()));
            logicIterator.next();
        }
    }

    private void processReadInstruction(LogicIterator logicIterator, ReadInstruction ix) {
        if (ix.getMemory().equals(LogicBuiltIn.THIS) && ix.getIndex() instanceof LogicString mlogName) {
            logicIterator.set(createSet(ix.getAstContext(),ix.getResult(), LogicVariable.mlogVariable(mlogName.getValue())));
        }
    }

    private void processWriteInstruction(LogicIterator logicIterator, WriteInstruction ix) {
        if (ix.getMemory().equals(LogicBuiltIn.THIS) && ix.getIndex() instanceof LogicString mlogName) {
            logicIterator.set(createSet(ix.getAstContext(),LogicVariable.mlogVariable(mlogName.getValue()), ix.getValue()));
        }
    }

    private void processReadArrInstruction(LogicIterator logicIterator, ReadArrInstruction ix) {
        ArrayStore arrayStore = ix.getArray().getArrayStore();
        if (ix.getIndex().isNumericConstant() && arrayStore.optimizeElementAccess()) {
            List<ValueStore> elements = arrayStore.getElements();
            if (!ix.getIndex().isLong()) {
                error(ix.getIndex().sourcePosition(), ERR.ARRAY_NON_INTEGER_INDEX);
            } else if (ix.getIndex().getIntValue() < 0 || ix.getIndex().getIntValue() >= elements.size()) {
                error(ix.getIndex().sourcePosition(), ERR.ARRAY_INDEX_OUT_OF_BOUNDS, elements.size() - 1);
            } else {
                LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(instructionProcessor,
                        ix.getAstContext(), logicIterator::add);
                logicIterator.remove();
                elements.get(ix.getIndex().getIntValue()).readValue(creator, ix.getResult());
            }
        }
    }

    private void processWriteArrInstruction(LogicIterator logicIterator, WriteArrInstruction ix) {
        ArrayStore arrayStore = ix.getArray().getArrayStore();
        if (ix.getIndex().isNumericConstant() && arrayStore.optimizeElementAccess()) {
            List<ValueStore> elements = arrayStore.getElements();
            if (!ix.getIndex().isLong()) {
                error(ix.getIndex().sourcePosition(), ERR.ARRAY_NON_INTEGER_INDEX);
            } else if (ix.getIndex().getIntValue() < 0 || ix.getIndex().getIntValue() >= elements.size()) {
                error(ix.getIndex().sourcePosition(), ERR.ARRAY_INDEX_OUT_OF_BOUNDS, elements.size() - 1);
            } else {
                LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(instructionProcessor,
                        ix.getAstContext(), logicIterator::add);
                logicIterator.remove();
                elements.get(ix.getIndex().getIntValue()).setValue(creator, ix.getValue());
            }
        }
    }
}
