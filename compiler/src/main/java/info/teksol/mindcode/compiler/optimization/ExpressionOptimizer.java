package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.ast.NumericLiteral;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.*;
import info.teksol.mindcode.processor.ExpressionEvaluator;

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
            final Tuple2<String, String> ops;
            if (it.next() instanceof OpInstruction ix && (ops = extractIdivOperands(ix)) != null) {
                String result = ix.getResult();
                List<LogicInstruction> list = findInstructions(
                        in -> in.getArgs().contains(result) && !(in instanceof PushOrPopInstruction));

                // Preconditions:
                // - exactly two instructions
                // - div/idiv/mul comes first
                // - the second is the floor operation
                // - the second operates on the result of the first
                if (list.size() == 2 && list.get(0) == ix && list.get(1) instanceof OpInstruction ox
                        && ox.getOperation().equals("floor") && ox.getFirstOperand().equals(ix.getResult())) {

                    replaceInstruction(ox, createInstruction(OP, "idiv", ox.getResult(), ops.getT1(), ops.getT2()));
                    it.remove();
                }
            }
        }

        return false;
    }

    private static final Pattern numLiteral = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");

    private Tuple2<String, String> extractIdivOperands(OpInstruction ix) {
        if (!isTemporary(ix.getResult())) {
            return null;
        } else {
            return switch (ix.getOperation()) {
                case "div", "idiv" -> new Tuple2<>(ix.getFirstOperand(), ix.getSecondOperand());

                case "mul" -> numLiteral.matcher(ix.getSecondOperand()).matches()
                        ? invertMultiplicand(ix.getFirstOperand(), ix.getSecondOperand())
                        : numLiteral.matcher(ix.getFirstOperand()).matches()
                        ? invertMultiplicand(ix.getSecondOperand(), ix.getFirstOperand())
                        : null;

                default -> null;
            };
        }
    }

    private Tuple2<String, String> invertMultiplicand(String variable, String literal) {
        try {
            double multiplicand = Double.parseDouble(literal);
            Optional<String> divisor = instructionProcessor.mlogRewrite(String.valueOf(1.0 / multiplicand));
            return divisor.map(lit -> new Tuple2<>(variable, lit)).orElse(null);
        } catch (NumberFormatException ex) {
            // Couldn't parse divisor. Do nothing.
            return null;
        }
    }
}
