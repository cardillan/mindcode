package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static info.teksol.mc.mindcode.logic.arguments.LogicBoolean.FALSE;

/// Turns the following sequence of instructions:
///
/// ```
/// op <comparison> var1 A B
/// jump label notEqual|equal var2 false
/// ```
///
/// into
///
/// ```
/// jump label <comparison|inverse of comparison> A B
/// ```
///
/// Requirements:
/// - jump condition is an notEqual/equal comparison to `false`
/// - `var1` and `var2` are identical
/// - `var1` is a `__tmp` variable
/// - `var1` is used only in the two affected instructions
/// - `<comparison>` can be converted to condition/has an inverse
@NullMarked
class JumpOptimizer extends BaseOptimizer {
    private @Nullable OpInstruction lastOp;
    private int lastOpIndex;

    JumpOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.JUMP_OPTIMIZATION, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {

        try (LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                LogicInstruction instruction = iterator.next();
                if (instruction instanceof OpInstruction op) {
                    lastOp = op;
                    lastOpIndex = iterator.currentIndex();
                } else if (lastOp != null
                        && instruction instanceof JumpInstruction jump
                        && jump.isConditional()
                        && lastOp.getResult().isTemporaryVariable()
                        && jump.getX().equals(lastOp.getResult())
                        && jump.getY() == FALSE) {

                    List<LogicInstruction> list = instructions(
                            ix -> ix.getArgs().contains(lastOp.getResult()) && !(ix instanceof PushOrPopInstruction));

                    // Not exactly two instructions
                    if (list.size() == 2 || identicalInstructions(list, lastOp, jump)) {
                        Operation operation = jump.getCondition() == Condition.EQUAL
                                ? lastOp.getOperation().hasInverse() ? lastOp.getOperation().inverse() : null
                                : lastOp.getOperation().toCondition() != null ? lastOp.getOperation() : null;

                        if (operation != null) {
                            iterator.set(createJump(jump.getAstContext(), jump.getTarget(), operation.toExistingCondition(),
                                    lastOp.getX(), lastOp.getY()));
                            removeInstruction(lastOpIndex);
                        }
                    }
                } else if (instruction instanceof LabelInstruction label) {
                    if (optimizationContext.isActive(label.getLabel())) {
                        lastOp = null;
                    }
                } else if (!(instruction instanceof NoOpInstruction)) {
                    lastOp = null;
                }

            }

            return false;
        }
    }

    public boolean identicalInstructions(List<LogicInstruction> instructions, OpInstruction op, JumpInstruction jump) {
        //if (true) return false;

        if (instructions.size() % 2 != 0) return false;
        for (int i = 2; i < instructions.size(); i += 2) {
            if (!op.equals(instructions.get(i))) return false;
            if (!(instructions.get(i + 1) instanceof JumpInstruction jump2
                    && jump2.getTypedArguments().subList(1, jump2.getTypedArguments().size() - 1).equals(
                            jump.getTypedArguments().subList(1, jump.getTypedArguments().size() - 1))))
                    return false;
        }

        return true;
    }
}