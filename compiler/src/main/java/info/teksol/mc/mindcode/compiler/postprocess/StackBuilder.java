package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.CompilerMessageEmitter;
import info.teksol.mc.mindcode.compiler.InstructionCounter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.StackParameters;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.StackTracker;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.InitStackInstruction;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NullMarked
public class StackBuilder extends CompilerMessageEmitter {
    private final InstructionProcessor processor;
    private final NameCreator nameCreator;
    private final CallGraph callGraph;
    private final OptimizationCoordinator optimizationCoordinator;
    private final List<LogicInstruction> program;

    private final AstContext rootAstContext;
    private final boolean symbolicLabels;

    private final LogicLabel returnLabel;
    private LogicLabel nextInitLabel;

    public StackBuilder(StackBuilderContext stackBuilderContext, OptimizationCoordinator optimizationCoordinator,
            List<LogicInstruction> program, InitStackInstruction initStackInstruction) {
        super(stackBuilderContext.messageConsumer());
        this.processor = stackBuilderContext.instructionProcessor();
        this.nameCreator = stackBuilderContext.nameCreator();
        this.rootAstContext = stackBuilderContext.rootAstContext();
        this.callGraph = stackBuilderContext.callGraph();
        this.optimizationCoordinator = optimizationCoordinator;
        this.program = program;
        this.symbolicLabels = optimizationCoordinator.getGlobalProfile().isSymbolicLabels();

        nextInitLabel = initStackInstruction.getCallLabel();
        returnLabel = initStackInstruction.getReturnLabel();
    }

    public static List<LogicInstruction> buildStack(StackBuilderContext stackBuilderContext, OptimizationCoordinator optimizationCoordinator,
            List<LogicInstruction> instructions) {
        StackTracker stackTracker = stackBuilderContext.stackTracker();
        CallGraph callGraph = stackBuilderContext.callGraph();
        if (stackTracker.externalStack() || !callGraph.containsRecursiveFunction()) return instructions;

        InstructionProcessor processor = stackBuilderContext.instructionProcessor();
        List<LogicInstruction> program = new ArrayList<>(instructions);

        int index = CollectionUtils.indexOf(program, 0, ix -> ix.getAstContext().matches(AstContextType.STACK));
        if (index < 0 || !(program.get(index) instanceof InitStackInstruction initStackInstruction)) {
            throw new MindcodeInternalError("No stack initialization found.");
        }

        return new StackBuilder(stackBuilderContext, optimizationCoordinator, program, initStackInstruction).buildStack();
    }

    private List<LogicInstruction> buildStack() {
        @SuppressWarnings("NullableProblems")
        List<StackParameters> stacks = callGraph.getFunctions().stream().filter(f -> f.isRecursive() && f.isGenerated())
                .map(function -> optimizationCoordinator.computeStackParameters(function, program))
                .filter(Objects::nonNull)
                .toList();
        if (stacks.isEmpty()) {
            return program;
        }

        int stackSize = stacks.stream().mapToInt(StackParameters::totalSize).sum();
        int availableSpace = optimizationCoordinator.getGlobalProfile().getInstructionLimit() - InstructionCounter.globalSize(program);
        if (stackSize > availableSpace) {
            error(ERR.STACK_SIZE_LIMIT_EXCEEDED, stackSize, availableSpace);
            return program;
        }

        if (symbolicLabels) {
            program.add(processor.createComment(rootAstContext, "The following instructions implement an internal stack"));
            program.add(processor.createComment(rootAstContext, "The program depends on absolute sizes of stack frames"));
            program.add(processor.createComment(rootAstContext, "Do not add or remove instructions below this point"));
        }

        for (int i = 0; i < stacks.size(); i++) {
            buildStack(stacks.get(i), i == stacks.size() - 1);
        }

        return program;
    }

    private void buildStack(StackParameters stack, boolean last) {
        MindcodeFunction function = stack.function();
        function.setStackFrameSize(stack.frameSize());
        function.setReturnOffset(stack.returnOffset());

        LogicLabel functionLabel = Objects.requireNonNull(function.getLabel());
        LogicLabel firstStackFrame = processor.nextLabel();
        function.setStackFrameLabel(firstStackFrame);

        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor,
                rootAstContext.createChild(function.getDeclaration(), AstContextType.STACK, AstSubcontextType.BASIC),
                program::add);

        if (symbolicLabels) {
            creator.createComment("");
            creator.createComment("Internal stack for function '" + function.getName() + "', stack depth: " + stack.depth());
            creator.createLabel(nextInitLabel);
            creator.createOp(Operation.ADD, function.getFnStackFrame(), LogicBuiltIn.COUNTER, LogicNumber.ONE);
            nextInitLabel = last ? returnLabel : processor.nextLabel();
            creator.createJumpUnconditional(nextInitLabel);
        }

        creator.createLabel(firstStackFrame);
        String transferSuffix = nameCreator.stackFrameSuffix(0);

        for (int frame = 1; frame <= stack.depth(); frame++) {
            String frameSuffix = nameCreator.stackFrameSuffix(frame);
            LogicVariable returnAddress = function.getFnRetAddr().stackFrame(frameSuffix);
            List<StackVariable> stackVariables = stack.variables().stream().map(v ->
                    new StackVariable(v, v.stackFrame(frameSuffix))).toList();

            creator.createSet(returnAddress, function.getFnRetAddr()).setComment("# Stack frame " + frame);
            stackVariables.forEach(v -> creator.createSet(v.stackFrame,
                    v.isParameter() ? v.original.stackFrame(transferSuffix) : v.original));
            creator.createJumpUnconditional(functionLabel);
            stackVariables.forEach(v -> creator.createSet(v.original, v.stackFrame));
            creator.createSet(LogicBuiltIn.COUNTER, returnAddress);
        }

        if (function.getProfile().isStackOverflowChecks()) {
            String message = String.format("Stack overflow calling function %s (call depth %d)", function.getName(), stack.depth() + 1);
            switch (function.getProfile().getErrorReporting()) {
                case ASSERT -> creator.createError(message);
                case MINIMAL, SIMPLE -> creator.createStop();
                case DESCRIBED -> {
                    creator.createPrint(LogicString.create(message));
                    creator.createStop();
                }
            }
            creator.createError(message);
        }
    }

    private record StackVariable(LogicVariable original, LogicVariable stackFrame) {
        boolean isParameter() {
            return original.getType() == ArgumentType.FUNCTION_PARAMETER;
        }
    }
}
