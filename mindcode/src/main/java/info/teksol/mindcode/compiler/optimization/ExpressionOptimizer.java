package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.compiler.instructions.PushOrPopInstruction;
import info.teksol.mindcode.compiler.instructions.SetInstruction;
import info.teksol.mindcode.logic.LogicLiteral;
import info.teksol.mindcode.logic.LogicNumber;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Operation;

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
public class ExpressionOptimizer extends BaseOptimizer {
    public ExpressionOptimizer(InstructionProcessor instructionProcessor) {
        super(Optimization.EXPRESSION_OPTIMIZATION, instructionProcessor);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase, int pass, int iteration) {
        // Cannot use for-each due to modifications of the underlying list in the loop
        try (LogicIterator it = createIterator()) {
            while(it.hasNext()) {
                switch (it.next()) {
                    case OpInstruction ix -> {
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
                                it.remove();
                            }
                        }
                    }
                    case SetInstruction set -> {
                        if (set.getResult().equals(set.getValue())) {
                            it.remove();
                        }
                    }
                    default -> {}
                }
            }
        }

        return false;
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
}
