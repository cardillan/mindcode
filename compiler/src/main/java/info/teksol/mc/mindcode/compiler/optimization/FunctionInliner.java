package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationContext.LogicList;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.instructions.EndInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.NoOpInstruction;
import info.teksol.mc.mindcode.logic.instructions.ReturnInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType.*;

/// Inlines functions
@NullMarked
class FunctionInliner extends BaseOptimizer {
    private int invocations = 0;
    private int count = 0;

    public FunctionInliner(OptimizationContext optimizationContext) {
        super(Optimization.FUNCTION_INLINING, optimizationContext);
    }

    @Override
    public void generateFinalMessages() {
        iterations = invocations;
        super.generateFinalMessages();
        if (count > 0) {
            emitMessage(MessageLevel.INFO, "%6d function calls inlined by %s.", count, getName());
        }
    }

    @Override
    protected boolean optimizeProgram(OptimizationPhase phase) {
        return false;
    }

    @Override
    public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
        invocations++;
        List<OptimizationAction> actions = new ArrayList<>();
        actions.addAll(forEachContext(AstContextType.FUNCTION, BODY,
                context -> findPossibleInlining(context, costLimit)));
        actions.addAll(forEachContext(c -> c.matches(OUT_OF_LINE_CALL),
                context -> findPossibleCallInlining(context, costLimit)));
        return actions;
    }


    private double computeInliningBenefit(AstContext call) {
        if (call.node() instanceof AstFunctionCall functionCall) {
            int arguments = functionCall.getArguments().stream()
                    .mapToInt(arg -> arg.hasExpression() ? arg.isInput() && arg.isOutput() ? 2 : 1 : 0).sum();

            // We assume that only half of the arguments will be successfully removed by the inlining.
            return call.totalWeight() * (3.0 + arguments / 2.0);
        } else {
            throw new MindcodeInternalError("Expected AstFunctionCall node, got " + call);
        }
    }

    private @Nullable OptimizationAction findPossibleInlining(AstContext context, int costLimit) {
        if (context.function() == null) {
            // The function is declared, but not used.
            return null;
        }
        MindcodeFunction function = context.function();
        if (function.isRecursive() || function.isInline() || function.cannotInline()) {
            return null;
        }

        List<AstContext> calls = contexts(
                c -> c.function() == context.function() && c.matches(AstContextType.CALL, OUT_OF_LINE_CALL));

        double benefit = calls.stream().mapToDouble(this::computeInliningBenefit).sum();

        // Cost: body size minus one (return) times number of calls minus body size (we'll remove the original)
        LogicList body = stripReturnInstructions(contextInstructions(context));
        if (body == null) {
            return null;
        }

        int bodySize = body.realSize();
        int cost = (bodySize - 1) * calls.size() - bodySize;

        return cost <= costLimit ? new InlineFunctionAction(context, cost, benefit) : null;
    }

    private @Nullable LogicList stripReturnInstructions(@Nullable LogicList body) {
        if (body == null || body.isEmpty()) {
            return null;
        }

        int index = body.size() - 1;
        while (index > 0 && (body.get(index) instanceof EndInstruction || body.get(index) instanceof NoOpInstruction)) {
            index--;
        }

        if (index <= 0) {
            return null;
        } else if (!(body.get(index) instanceof ReturnInstruction)) {
            throw new MindcodeInternalError("Unexpected function body structure.");
        }

        return body.subList(0, index);
    }

    private OptimizationResult inlineFunction(AstContext context, int costLimit) {
        MindcodeFunction function = Objects.requireNonNull(context.function());
        if (function.isRecursive() || function.isInline()) {
            return OptimizationResult.INVALID;
        }

        LogicList body = stripReturnInstructions(contextInstructions(context));
        if (body == null) {
            return OptimizationResult.INVALID;
        }

        List<AstContext> calls = contexts(
                c -> c.function() == context.function() && c.matches(AstContextType.CALL, OUT_OF_LINE_CALL));

        List<LogicInstruction> callIxs = calls.stream()
                .flatMap(call -> contextStream(call.parent())
                        .filter(ix -> ix.getOpcode() == Opcode.CALL && ix.getAstContext() == call))
                .toList();

        if (calls.size() != callIxs.size()) {
            return OptimizationResult.INVALID;
        }

        trace(() -> "Inlining function " + function.getName());

        for (int i = 0; i < calls.size(); i++) {
            AstContext call = calls.get(i);
            LogicInstruction cix = callIxs.get(i);

            assert call.parent() != null;
            AstContext newContext = call.parent().createSubcontext(INLINE_CALL, 1.0);
            int insertionPoint = firstInstructionIndex(call);
            LogicList newBody = body.duplicateToContext(newContext);
            insertInstructions(insertionPoint, newBody);
            // Remove original call instructions
            removeMatchingInstructions(ix -> ix.belongsTo(call));
            if (!LogicLabel.EMPTY.equals(cix.getHoistId())) {
                removeMatchingInstructions(ix -> ix.getHoistId().equals(cix.getHoistId()));
            }
        }

        // Remove original function
        removeMatchingInstructions(ix -> ix.belongsTo(context));

        function.setInlined();
        count += calls.size();

        return OptimizationResult.REALIZED;
    }

    private class InlineFunctionAction extends AbstractOptimizationAction {
        public InlineFunctionAction(AstContext astContext, int cost, double benefit) {
            super(astContext, cost, benefit);
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(() -> inlineFunction(astContext(), costLimit), toString());
        }

        @Override
        public String toString() {
            assert astContext.node() != null;
            assert astContext.function() != null;
            return "Inline function '" + astContext.function().getName() + "' defined at " + astContext.node().sourcePosition().formatForLog();
        }
    }



    private @Nullable OptimizationAction findPossibleCallInlining(AstContext call, int costLimit) {
        if (call.function() == null) {
            // Shouldn't happen here
            return null;
        }
        MindcodeFunction function = call.function();
        if (function.isRecursive() || function.isInline() || function.cannotInline()) {
            return null;
        }

        double benefit = computeInliningBenefit(call);

        // Need to find the function body
        LogicList body = stripReturnInstructions(
                contextInstructions(
                        existingContext(
                                c -> c.function() == call.function()
                                     && c.matches(AstContextType.FUNCTION, BODY)
                        )
                )
        );
        if (body == null) {
            return null;
        }
        // Cost: body size minus one (return) times the number of calls minus body size (we'll remove the original)
        int cost = body.realSize() - 1;
        return cost <= costLimit ? new InlineFunctionCallAction(call, cost, benefit) : null;
    }

    private OptimizationResult inlineFunctionCall(AstContext call, int costLimit) {
        Optional<LogicInstruction> cix = contextStream(call.parent())
                .filter(ix -> ix.getOpcode() == Opcode.CALL && ix.getAstContext() == call).findFirst();
        MindcodeFunction function = call.existingFunction();
        if (cix.isEmpty() || function.isRecursive() || function.isInline()) {
            return OptimizationResult.INVALID;
        }

        LogicList body = stripReturnInstructions(
                contextInstructions(
                        existingContext(c -> c.function() == call.function()
                                        && c.matches(AstContextType.FUNCTION, BODY)
                        )
                )
        );
        if (body == null) {
            return OptimizationResult.INVALID;
        }

        trace(() -> "Inlining call " + call + " of function " + function.getName());

        AstContext newContext = Objects.requireNonNull(call.parent())
                .createSubcontext(INLINE_CALL, 1.0);
        int insertionPoint = firstInstructionIndex(call);
        LogicList newBody = body.duplicateToContext(newContext);
        insertInstructions(insertionPoint, newBody);
        // Remove original call instructions, including hoisted ones
        removeMatchingInstructions(ix -> ix.belongsTo(call));
        if (!LogicLabel.EMPTY.equals(cix.get().getHoistId())) {
            removeMatchingInstructions(ix -> ix.getHoistId().equals(cix.get().getHoistId()));
        }

        count += 1;
        return OptimizationResult.REALIZED;
    }

    private class InlineFunctionCallAction extends AbstractOptimizationAction {
        public InlineFunctionCallAction(AstContext astContext, int cost, double benefit) {
            super(astContext, cost, benefit);
        }

        @Override
        public OptimizationResult apply(int costLimit) {
            return applyOptimization(() -> inlineFunctionCall(astContext(), costLimit), toString());
        }

        @Override
        public String toString() {
            assert astContext.node() != null;
            return "Inline function call at " + astContext.node().sourcePosition().formatForLog();
        }
    }
}
