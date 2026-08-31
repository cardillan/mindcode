package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.ContextFactory;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.StackTracker;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public class InitStackInstruction extends BaseInstruction {
    private final @Nullable CallGraph callGraph;
    private final @Nullable StackTracker stackTracker;

    InitStackInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.INITSTACK, args, params);
        if (ContextFactory.isMasterContextSet()) {
            callGraph = ContextFactory.getMasterContext().callGraph();
            stackTracker = ContextFactory.getMasterContext().stackTracker();
        } else {
            callGraph = null;
            stackTracker = null;
        }
    }

    protected InitStackInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
        if (ContextFactory.isMasterContextSet()) {
            callGraph = ContextFactory.getMasterContext().callGraph();
            stackTracker = ContextFactory.getMasterContext().stackTracker();
        } else {
            callGraph = null;
            stackTracker = null;
        }
    }

    public LogicLabel getCallLabel() {
        return (LogicLabel) getArg(0);
    }

    public LogicLabel getReturnLabel() {
        return (LogicLabel) getArg(1);
    }

    @Override
    public InitStackInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new InitStackInstruction(this, astContext);
    }

    @Override
    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        if (callGraph == null || stackTracker == null) return 0;

        List<MindcodeFunction> recursiveFunctions = callGraph.getFunctions().stream()
                .filter(f -> f.isRecursive() && f.isGenerated()).toList();
        if (recursiveFunctions.isEmpty()) return 0;

        boolean symbolic = getAstContext().getGlobalProfile().isSymbolicLabels();
        int spInit = stackTracker.externalStack() || symbolic ? 1 : recursiveFunctions.size();
        return spInit + recursiveFunctions.stream().mapToInt(f -> f.getArrays().size()).sum();
    }
}
