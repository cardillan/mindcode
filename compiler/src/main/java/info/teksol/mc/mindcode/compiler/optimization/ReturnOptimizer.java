package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.InstructionCounter;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.instructions.EndInstruction;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.ReturnRecInstruction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.BODY;

@NullMarked
class ReturnOptimizer extends BaseOptimizer {
    public ReturnOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.RETURN_OPTIMIZATION, optimizationContext);
    }

    private int invocations = 0;
    private int count = 0;

    @Override
    public void generateFinalMessages() {
        iterations = invocations;
        super.generateFinalMessages();
        if (count > 0) {
            emitMessage(MessageLevel.INFO, "%6d return statements optimized by %s.", count, getName());
        }
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        return false;
    }

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        invocations++;
        return forEachContext(AstContextType.FUNCTION, BODY, this::findReturnStatementOptimizations)
                .stream()
                .flatMap(List::stream)
                .toList();
    }

    private List<OptimizationAction> findReturnStatementOptimizations(AstContext context) {
        if (context.function() == null || !context.function().isRecursive()) {
            return List.of();
        }

        LogicList body = contextInstructions(context);
        ReturnRecInstruction returnRecInstruction = findReturnInstruction(body);
        if (returnRecInstruction == null || body.stream().noneMatch(ix -> isJumpToReturn(ix, returnRecInstruction))) {
            return List.of();
        }

        return body.stream()
                .filter(ix -> isJumpToReturn(ix, returnRecInstruction))
                .map(JumpInstruction.class::cast)
                .map(ix -> createOptimizationAction(ix, returnRecInstruction))
                .toList();
    }

    private boolean isJumpToReturn(LogicInstruction ix, ReturnRecInstruction returnRecInstruction) {
        return ix instanceof JumpInstruction jump
                && jump.isUnconditional()
                && labeledInstruction(jump.getTarget()) == returnRecInstruction
                && !hasNoCode(instructionSubList(jump, returnRecInstruction));
    }

    private boolean hasNoCode(List<LogicInstruction> instructions) {
        return InstructionCounter.localSize(instructions.subList(1, instructions.size() - 1)) == 0;
    }

    private @Nullable ReturnRecInstruction findReturnInstruction(LogicList body) {
        if (body.isEmpty()) {
            return null;
        }

        int index = body.getLast() instanceof EndInstruction ? 1 : 0;
        return body.getFromEnd(index) instanceof ReturnRecInstruction ret ? ret : null;
    }

    private OptimizationAction createOptimizationAction(JumpInstruction ix, ReturnRecInstruction returnRecInstruction) {
        int cost = returnRecInstruction.getRealSize(null) - ix.getRealSize(null);
        double benefit = ix.getRealSize(null) * ix.getAstContext().totalWeight();
        return new ReplaceReturnStatementAction(ix.getAstContext(), cost, benefit, ix, returnRecInstruction);
    }

    private OptimizationResult replaceJumpToReturn(ReplaceReturnStatementAction action, int costLimit) {
        int index = instructionIndex(action.original);
        if (index < 0) {
            return OptimizationResult.INVALID;
        }

        LogicInstruction replacement = action.replacement.withContext(action.original.getAstContext());
        replaceInstruction(index, replacement);
        count++;
        return OptimizationResult.REALIZED;
    }

    private class ReplaceReturnStatementAction extends AbstractOptimizationAction {
        private final JumpInstruction original;
        private final ReturnRecInstruction replacement;

        public ReplaceReturnStatementAction(AstContext astContext, int cost, double benefit,
                JumpInstruction original, ReturnRecInstruction replacement) {
            super(astContext, cost, benefit);
            this.original = original;
            this.replacement = replacement;
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(() -> replaceJumpToReturn(this, costLimit), toString());
        }

        @Override
        public String toString() {
            assert astContext.node() != null;
            return "Optimize return at " + astContext.node().sourcePosition().formatForLog();
        }
    }
}
