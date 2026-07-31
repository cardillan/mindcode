package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.InstructionCounter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.builders.AbstractLoopBuilder;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicIterator;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;

@NullMarked
class RecursiveOptimizer extends BaseOptimizer {
    public RecursiveOptimizer(OptimizationContext optimizationContext) {
        super(Optimization.RECURSIVE_OPTIMIZATION, optimizationContext);
    }

    private int invocations = 0;
    private int returnReplacementCount = 0;
    private int tailCallCount = 0;

    @Override
    public void generateFinalMessages() {
        iterations = invocations;
        super.generateFinalMessages();
        if (returnReplacementCount > 0) {
            emitMessage("%6d return statement§ optimized by %s.", returnReplacementCount, getName());
        }
        if (tailCallCount > 0) {
            emitMessage("%6d tail call§ eliminated by %s.", tailCallCount, getName());
        }
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        forEachContext(AstContextType.FUNCTION_DEF, BODY, context -> {
            optimizeFunction(context);
            return null;
        });

        return false;
    }

    private void optimizeFunction(AstContext context) {
        MindcodeFunction function = context.function();
        if (!advanced(context) || function == null || !function.isRecursive()) return;

        LogicList body = contextInstructions(context);
        int lastIndex = body.getLastReal() instanceof EndInstruction ? 1 : 0;
        if (body.getRealFromEnd(lastIndex) instanceof ReturnRecInstruction ret
                && body.getRealFromEnd(lastIndex + 1) instanceof CallRecInstruction call
                && call.getCallAddr().equals(function.getLabel())) {

            AstMindcodeNode node = Objects.requireNonNull(context.node());
            AstContext loopContext = context.createChild(node, AstContextType.LOOP, AstSubcontextType.BASIC);
            AstContext currContext = loopContext.createSubcontext(BODY, AbstractLoopBuilder.LOOP_REPETITIONS);

            LogicList loop = body.duplicateToContext(currContext, label -> !label.equals(function.getLabel()));

            LogicLabel startLabel = instructionProcessor.nextLabel();
            boolean moveToCurrContext = false;

            try (LogicIterator it = optimizationContext.createIteratorAtContext(context)) {
                it.next();  // Skip function label
                it.add(instructionProcessor.createLabel(currContext, startLabel));

                for (int index = 1; index < body.size(); index++) {
                    LogicInstruction original = it.next();
                    LogicInstruction replacement = Objects.requireNonNull(loop.get(index));

                    if (original == call) {
                        currContext = loopContext.createSubcontext(FLOW_CONTROL, AbstractLoopBuilder.LOOP_REPETITIONS);
                        moveToCurrContext = true;
                        it.set(instructionProcessor.createJumpUnconditional(currContext, startLabel));
                        currContext = context;
                    } else if (moveToCurrContext) {
                        it.set(replacement.withContext(currContext));
                    } else {
                        it.set(replacement);
                    }
                }
            }

            // The optimization might have turned the function non-recursive
            boolean nonRecursive = body.stream().noneMatch(ix -> ix instanceof CallRecInstruction c && c != call && c.getAstContext().function() == function);

            if (nonRecursive) {
                // The function is no longer recursive. Replace all recursive calls with non-recursive ones.
                function.removeRecursiveCall(function);

                List<AstContext> rebuildCalls = new ArrayList<>();

                try (LogicIterator it = optimizationContext.createIteratorAtIndex(0)) {
                    while (it.hasNext()) {
                        switch (it.next()) {
                            case CallRecInstruction c when c.getAstContext().function() == function -> {
                                rebuildCalls.add(c.getAstContext().existingParent());
                            }

                            case ReturnRecInstruction r when r.getAstContext().function() == function -> {
                                if (r.getJumpToReturn().isPresent()) {
                                    LogicLabel newTarget = loop.getLabelMap().get(((JumpInstruction)r.getJumpToReturn().get()).getTarget());
                                    returnReplacementCount--;
                                    it.set(instructionProcessor.createJumpUnconditional(r.getAstContext(), newTarget));
                                } else {
                                    it.set(instructionProcessor.createReturn(r.getAstContext(), function.getFnRetAddr()));
                                }
                            }

                            default -> {
                            }
                        }
                    }
                }

                for (AstContext callContext : rebuildCalls) {
                    int dest = optimizationContext.firstInstructionIndex(callContext);
                    List<AstContext> contexts = callContext.children();
                    List<LogicList> bodies = contexts.stream().map(this::contextInstructions).toList();
                    optimizationContext.removeMatchingInstructions(ix -> ix.belongsTo(callContext));

                    for (int i = 0; i < bodies.size(); i++) {
                        switch (contexts.get(i).subcontextType()) {
                            case STACK -> {
                            }
                            case PARAMETERS -> dest = insertInstructions(dest, bodies.get(i));
                            case RECURSIVE_CALL -> {
                                AstContext oolContext = callContext.createSubcontext(function, OUT_OF_LINE_CALL, 1.0);
                                CallRecInstruction c = (CallRecInstruction) bodies.get(i).getExisting(0);
                                LabelInstruction l = (LabelInstruction) bodies.get(i).getExisting(1);

                                if (function.getProfile().isSymbolicLabels()) {
                                    insertInstruction(dest++, instructionProcessor.createCallStackless(oolContext,
                                            c.getCallAddr(), function.getFnRetAddr(), function.getFnRetVal()));
                                } else {
                                    if (!l.getLabel().equals(c.getRetAddr())) {
                                        throw new MindcodeInternalError("Unexpected context structure.");
                                    }
                                    AstContext paramContext = contexts.getFirst().matches(PARAMETERS)
                                            ? contexts.getFirst() : callContext.createSubcontext(function, PARAMETERS, 1.0);

                                    insertInstruction(dest++, instructionProcessor.createSetAddress(paramContext,
                                            function.getFnRetAddr(), c.getRetAddr()).setHoistId(c.getRetAddr()));
                                    insertInstruction(dest++, instructionProcessor.createCallStackless(oolContext,
                                                    c.getCallAddr(), LogicVariable.INVALID, function.getFnRetVal())
                                            .setMarker(c.getRetAddr()).setHoistId(c.getRetAddr()));
                                }

                                insertInstruction(dest++, l.withContext(oolContext));
                            }
                        }
                    }
                }
            }

            tailCallCount++;
        }
    }

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        invocations++;
        return forEachContext(AstContextType.FUNCTION_DEF, BODY, this::findReturnStatementOptimizations)
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
        int cost = returnRecInstruction.getRealSize() - ix.getRealSize();
        double benefit = ix.getRealSize() * ix.getAstContext().totalWeight();
        return new ReplaceReturnStatementAction(ix.getAstContext(), cost, benefit, ix, returnRecInstruction);
    }

    private OptimizationResult replaceJumpToReturn(ReplaceReturnStatementAction action, int costLimit) {
        int index = instructionIndex(action.original);
        if (index < 0) {
            return OptimizationResult.INVALID;
        }

        LogicInstruction replacement = action.replacement.withContext(action.original.getAstContext())
                .setJumpToReturn(Optional.of(action.original));
        replaceInstruction(index, replacement);
        returnReplacementCount++;
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
