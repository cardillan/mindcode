package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.JumpInstruction;
import info.teksol.mindcode.compiler.instructions.LabelInstruction;

/**
 * This optimizer detects situations where a conditional jump skips a following, unconditional one and replaces it
 * with a single conditional jump with a reversed condition and the target of the second jump. Example:
 * <pre>{@code
 * jump __label0 equal __tmp9 false
 * jump __label1
 * label __label0
 * }</pre>
 * will be turned to
 * <pre>{@code
 * jump __label1 notEqual __tmp9 false
 * }</pre>
 * Optimization won't be done if the condition doesn't have an inverse (i.e. {@code ===}).
 * <p>
 * These sequences of instructions may arise when using break or continue statements:
 * <pre>{@code
 * while true
 *     ...
 *     if not_alive
 *         break
 *     end
 * end
 * }</pre>
 */
public class JumpOverJumpEliminator extends BaseOptimizer {

    public JumpOverJumpEliminator(InstructionProcessor instructionProcessor) {
        super(instructionProcessor);
    }

    @Override
    protected boolean optimizeProgram() {
        try (LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                if (iterator.next() instanceof JumpInstruction jump
                        && jump.getCondition().hasInverse()
                        && iterator.peek(0) instanceof JumpInstruction next
                        && next.isUnconditional()) {

                    try (LogicIterator inner = iterator.copy()) {
                        inner.next(); // Skip unconditional jump
                        while (inner.hasNext() && inner.next() instanceof LabelInstruction label) {
                            if (label.getLabel().equals(jump.getTarget())) {
                                iterator.set(jump.invert().withTarget(next.getTarget()));
                                iterator.next();
                                iterator.remove();
                                break;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}
