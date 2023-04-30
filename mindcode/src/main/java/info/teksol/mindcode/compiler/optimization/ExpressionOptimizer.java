package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.OpInstruction;
import info.teksol.mindcode.compiler.instructions.PushOrPopInstruction;
import info.teksol.mindcode.logic.*;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static info.teksol.mindcode.logic.Opcode.OP;

/**
 * This optimizer improves and streamlines expressions.
 * <p>
 * Mul/div + floor optimization:
 * <pre>{@code
 * op mul __tmp variable1 constant
 * op floor variable2 __tmp
 * }</pre>
 * is replaced by
 * <pre>{@code
 * op idiv variable2 variable1 (1 / constant)
 * }</pre>
 * where 1 / constant is evaluated here. If the instruction is div instead of mul, the inverse isn't taken.
 * </p>
 */
public class ExpressionOptimizer extends GlobalOptimizer {
    public ExpressionOptimizer(InstructionProcessor instructionProcessor, LogicInstructionPipeline next) {
        super(instructionProcessor, next);
    }

    @Override
    protected boolean optimizeProgram() {
        // Cannot use for-each due to modifications of the underlying list in the loop
        for (Iterator<LogicInstruction> it = program.iterator(); it.hasNext(); ) {
            final Tuple2<LogicValue, LogicValue> ops;
            if (it.next() instanceof OpInstruction ix && (ops = extractIdivOperands(ix)) != null) {
                LogicVariable result = ix.getResult();
                List<LogicInstruction> list = findInstructions(in -> in.getArgs().contains(result) && !(in instanceof PushOrPopInstruction));

                // Preconditions:
                // - exactly two instructions
                // - div/idiv/mul comes first
                // - the second is the floor operation
                // - the second operates on the result of the first
                if (list.size() == 2 && list.get(0) == ix && list.get(1) instanceof OpInstruction ox
                        && ox.getOperation() == Operation.FLOOR && ox.getFirstOperand().equals(ix.getResult())) {

                    replaceInstruction(ox, createInstruction(OP, Operation.IDIV, ox.getResult(), ops.getT1(), ops.getT2()));
                    it.remove();
                }
            }
        }

        return false;
    }

    private static final Pattern numLiteral = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");

    private Tuple2<LogicValue, LogicValue> extractIdivOperands(OpInstruction ix) {
        if (ix.getResult().getType() != ArgumentType.TMP_VARIABLE) {
            return null;
        } else {
            return switch (ix.getOperation()) {
                case DIV, IDIV -> new Tuple2<>(ix.getFirstOperand(), ix.getSecondOperand());

                case MUL ->
                        ix.getFirstOperand().isLiteral() ? invertMultiplicand(ix.getSecondOperand(), ix.getFirstOperand()) :
                        ix.getSecondOperand().isLiteral() ? invertMultiplicand(ix.getFirstOperand(), ix.getSecondOperand()) :
                        null;

                default -> null;
            };
        }
    }

    private Tuple2<LogicValue, LogicValue> invertMultiplicand(LogicValue variable, LogicValue literal) {
        // If literal.getDoubleValue() returns NaN, the NaN will make it through into the mlogFormat, which will
        // return an empty optional.
        double multiplicand = literal.getDoubleValue();
        double divisor = 1.0d / multiplicand;
        Optional<String> inverted = instructionProcessor.mlogFormat(divisor);
        return inverted.map(lit -> Tuple2.ofSame(variable, LogicNumber.get(lit, divisor))).orElse(null);
    }
}
