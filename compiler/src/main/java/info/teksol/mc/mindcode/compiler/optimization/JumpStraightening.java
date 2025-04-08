package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import info.teksol.mc.mindcode.logic.instructions.LabelInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import org.jspecify.annotations.NullMarked;

/// This optimizer detects situations where a conditional jump skips a following, unconditional one and replaces it
/// with a single conditional jump with a reversed condition and the target of the second jump. Example:
///
/// ```
/// jump __label0 equal __tmp9 false
/// jump __label1
/// label __label0
/// ```
///
/// will be turned to
///
/// ```
/// jump __label1 notEqual __tmp9 false
/// ```
///
/// Optimization won't be done if the condition doesn't have an inverse (i.e. `===`).
///
/// These sequences of instructions may arise when using break or continue statements:
///
/// ```
/// while true do
///     ...
///     if not_alive then
///         break;
///     end;
/// end;
/// ```
@NullMarked
class JumpStraightening extends BaseOptimizer {

    public JumpStraightening(OptimizationContext optimizationContext) {
        super(Optimization.JUMP_STRAIGHTENING, optimizationContext);
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        try (LogicIterator iterator = createIterator()) {
            while (iterator.hasNext()) {
                if (iterator.next() instanceof JumpInstruction jump
                        && jump.getCondition().hasInverse()
                        && iterator.hasNext() && iterator.peek(0) instanceof JumpInstruction next
                        && next.isUnconditional()) {

                    if (next.getAstContext() == jump.getAstContext() || phase.breaksContextStructure()) {
                        try (LogicIterator inner = iterator.copy()) {
                            inner.next(); // Skip unconditional jump
                            while (inner.hasNext()) {
                                LogicInstruction instruction = inner.next();
                                if (instruction instanceof LabelInstruction label) {
                                    if (label.getLabel().equals(jump.getTarget())) {
                                        iterator.set(jump.invert().withTarget(next.getTarget()));
                                        AstContext astContext = iterator.next().getAstContext();
                                        iterator.set(createNoOp(astContext));
                                        break;
                                    }
                                } else if (instruction.isReal()) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}
