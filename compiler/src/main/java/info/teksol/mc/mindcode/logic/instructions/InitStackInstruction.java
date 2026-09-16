package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.ContextFactory;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.StackTracker;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class InitStackInstruction extends BaseInstruction {
    private final @Nullable CallGraph callGraph;
    private final @Nullable StackTracker stackTracker;

    InitStackInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.INITSTACK, args, params);
        callGraph =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().callGraph() : null;
        stackTracker =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().stackTracker() : null;
    }

    protected InitStackInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
        callGraph =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().callGraph() : null;
        stackTracker =  ContextFactory.isMasterContextSet() ? ContextFactory.getMasterContext().stackTracker() : null;
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
        int spInit;
        if (stackTracker.externalStack()) {
            int elements = stackTracker.getStackStorage().size();
            spInit = elements == 1 ? 1 : 1 + elements * 2;
        } else {
            spInit = symbolic ? 1 : recursiveFunctions.size();
        }
        return spInit + recursiveFunctions.stream().mapToInt(f -> f.getArrays().size()).sum();
    }

    @Override
    public void resolve(InstructionProcessor processor, Consumer<LogicInstruction> consumer) {
        LocalContextfulInstructionsCreator creator = creator(processor, consumer);

        assert stackTracker != null;
        assert callGraph != null;
        LogicVariable stackPointer = stackTracker.getStackPointer();
        LogicVariable stackMemory = stackTracker.getStackMemory();

        List<MindcodeFunction> recursiveFunctions = callGraph.getFunctions().stream()
                .filter(f -> f.isRecursive() && f.isGenerated()).toList();

        if (!recursiveFunctions.isEmpty()) {
            if (stackTracker.externalStack()) {
                List<LogicVariable> storage = stackTracker.getStackStorage();
                if (storage.size() > 1) {
                    for (int i = 1; i < storage.size(); i++) {
                        creator.createWrite(storage.get(i), storage.get(i - 1), LogicNumber.ZERO);
                        creator.createWrite(storage.get(i - 1), storage.get(i), LogicNumber.ONE);
                    }
                    creator.createWrite(LogicNumber.ZERO, storage.getLast(), LogicNumber.ZERO);
                    creator.createSet(stackMemory, storage.getFirst());
                    creator.createSet(stackPointer, LogicNumber.FOUR);
                } else {
                    creator.createSet(stackPointer, LogicNumber.create(stackTracker.getAllocationStart()));
                }
            } else if (astContext.getGlobalProfile().isSymbolicLabels()) {
                creator.createJumpUnconditional(getCallLabel());
                creator.createLabel(getReturnLabel());
            } else {
                recursiveFunctions.forEach(function -> creator.createInstruction(Opcode.SET,
                        function.getFnStackFrame(), function.getStackFrameLabel()));
            }

            // Setup array indexes
            recursiveFunctions.stream().flatMap(f -> f.getArrays().stream())
                    .forEach(a -> creator.createSet((LogicVariable) a.getArrayOffset(), LogicNumber.create(-a.getSize())));
        }
    }
}
