package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.instructions.EndInstruction;
import info.teksol.mc.mindcode.logic.instructions.JumpInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.ReturnInstruction;

import java.util.List;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.BODY;

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
        ReturnInstruction returnInstruction = findReturnInstruction(body);
        if (returnInstruction == null || body.stream().noneMatch(ix -> isJumpToReturn(ix, returnInstruction))) {
            return List.of();
        }

        return body.stream()
                .filter(ix -> isJumpToReturn(ix, returnInstruction))
                .map(JumpInstruction.class::cast)
                .map(ix -> createOptimizationAction(ix, returnInstruction))
                .toList();
    }

    private boolean isJumpToReturn(LogicInstruction ix, ReturnInstruction returnInstruction) {
        return ix instanceof JumpInstruction jump
                && jump.isUnconditional()
                && labeledInstruction(jump.getTarget()) == returnInstruction
                && !hasNoCode(instructionSubList(jump, returnInstruction));
    }

    private boolean hasNoCode(List<LogicInstruction> instructions) {
        return instructions.subList(1, instructions.size() - 1).stream()
                .mapToInt(LogicInstruction::getRealSize).sum() == 0;
    }

    private ReturnInstruction findReturnInstruction(LogicList body) {
        if (body == null || body.isEmpty()) {
            return null;
        }

        int index = body.getLast() instanceof EndInstruction ? 1 : 0;
        return body.getFromEnd(index) instanceof ReturnInstruction ret ? ret : null;
    }

    private OptimizationAction createOptimizationAction(JumpInstruction ix, ReturnInstruction returnInstruction) {
        int cost = returnInstruction.getRealSize() - ix.getRealSize();
        double benefit = ix.getRealSize() * ix.getAstContext().totalWeight();
        return new ReplaceReturnStatementAction(ix.getAstContext(), cost, benefit, ix, returnInstruction);
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
        private final ReturnInstruction replacement;

        public ReplaceReturnStatementAction(AstContext astContext, int cost, double benefit,
                JumpInstruction original, ReturnInstruction replacement) {
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
            return "Optimize return at " + astContext.node().inputPosition().formatForLog();
        }
    }
}