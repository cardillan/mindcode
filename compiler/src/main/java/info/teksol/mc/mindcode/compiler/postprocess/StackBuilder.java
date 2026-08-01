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
import info.teksol.mc.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LabelInstruction;
import info.teksol.mc.mindcode.logic.instructions.LocalContextfulInstructionsCreator;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@NullMarked
public class StackBuilder extends CompilerMessageEmitter {
    private final InstructionProcessor processor;
    private final CallGraph callGraph;
    private final OptimizationCoordinator optimizationCoordinator;
    private final List<LogicInstruction> program;

    private final AstContext rootAstContext;
    private final AstContext initializationContext;
    private final boolean symbolicLabels;

    private LogicLabel nextInitLabel;
    private int initializationIndex = 0;

    public StackBuilder(InstructionProcessor processor, AstContext rootAstContext, CallGraph callGraph,
            OptimizationCoordinator optimizationCoordinator, List<LogicInstruction> program, int initializationIndex) {
        super(processor.messageConsumer());
        this.processor = processor;
        this.rootAstContext = rootAstContext;
        this.callGraph = callGraph;
        this.optimizationCoordinator = optimizationCoordinator;
        this.program = program;
        this.initializationContext = program.get(initializationIndex).getAstContext();
        this.symbolicLabels = optimizationCoordinator.getGlobalProfile().isSymbolicLabels();

        this.nextInitLabel = processor.nextLabel();
        this.initializationIndex = initializationIndex;
        program.remove(initializationIndex);
    }

    public static List<LogicInstruction> buildStack(InstructionProcessor processor, AstContext rootAstContext, CallGraph callGraph,
            OptimizationCoordinator optimizationCoordinator, StackTracker stackTracker, List<LogicInstruction> instructions) {
        if (callGraph.containsRecursiveFunction()) {
            List<LogicInstruction> program = new ArrayList<>(instructions);
            int index = CollectionUtils.indexOf(program, 0, ix -> ix.getAstContext().matches(AstContextType.STACK));
            if (index < 0) {
                throw new MindcodeInternalError("No stack initialization found.");
            }

            if (stackTracker.externalStack()) {
                // External stack
                program.set(index, processor.createSet(instructions.get(index).getAstContext(), processor.stackPointer(), LogicNumber.create(stackTracker.getAllocationStart())));
                return program;
            } else {
                return new StackBuilder(processor, rootAstContext, callGraph, optimizationCoordinator, program, index).buildStack();
            }
        } else {
            return instructions.stream().filter(ix -> !ix.getAstContext().matches(AstContextType.STACK)).toList();
        }
    }

    private List<LogicInstruction> buildStack() {
        @SuppressWarnings("NullableProblems")
        List<StackParameters> stacks = callGraph.getFunctions().stream().filter(f -> f.isRecursive() && f.isGenerated())
                .map(optimizationCoordinator::computeStackParameters)
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
            program.add(initializationIndex++, processor.createJumpUnconditional(initializationContext,nextInitLabel));
            program.add(processor.createComment(rootAstContext, "The following instructions implement an internal stack"));
            program.add(processor.createComment(rootAstContext, "The program depends on absolute sizes of stack frames"));
            program.add(processor.createComment(rootAstContext, "Do not add or remove instructions below this point"));
        }

        // TODO: increase stack sizes if possible

        stacks.forEach(this::buildStack);

        if (symbolicLabels) {
            program.add(initializationIndex++, processor.createLabel(initializationContext, nextInitLabel));
        }
        return program;
    }

    private void buildStack(StackParameters stack) {
        MindcodeFunction function = stack.function();
        function.setStackFrameSize(stack.frameSize());
        function.setReturnOffset(stack.returnOffset());

        LogicLabel functionLabel = Objects.requireNonNull(function.getLabel());
        LogicLabel firstStackFrame = processor.nextLabel();

        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor,
                rootAstContext.createChild(function.getDeclaration(), AstContextType.STACK, AstSubcontextType.BASIC),
                program::add);

        if (symbolicLabels) {
            creator.createComment("");
            creator.createComment("Internal stack for function '" + function.getName() + "', stack depth: " + stack.depth());
            creator.createLabel(nextInitLabel);
            creator.createOp(Operation.ADD, function.getFnStackFrame(), LogicBuiltIn.COUNTER, LogicNumber.ONE);
            nextInitLabel = processor.nextLabel();
            creator.createJumpUnconditional(nextInitLabel);
        } else {
            program.add(initializationIndex++, processor.createSetAddress(initializationContext, function.getFnStackFrame(), firstStackFrame));
        }

        creator.createLabel(firstStackFrame);

        for (int f = 1; f <= stack.depth(); f++) {
            int frame = f;
            Supplier<String> commentSupplier = new Supplier<>() {
                final String comment = "# Stack frame " + frame;
                int counter = 0;

                @Override
                public String get() {
                    return counter++ == 0 ? comment : "";
                }
            };

            stack.variables().forEach(v -> creator.createSet(v.stackFrame(frame),
                    v.getType() == ArgumentType.FUNCTION_PARAMETER ? v.stackFrame(0) : v).setComment(commentSupplier.get()));
            creator.createSet(function.getFnRetAddr().stackFrame(frame), function.getFnRetAddr()).setComment(commentSupplier.get());
            creator.createJumpUnconditional(functionLabel);
            stack.variables().forEach(v -> creator.createSet(v, v.stackFrame(frame)));
            creator.createSet(LogicBuiltIn.COUNTER, function.getFnRetAddr().stackFrame(frame));
        }

        String message = String.format("Stack overflow calling function %s (call depth %d)", function.getName(), stack.depth() + 1);
        creator.createError(message);

        // Decorate the function itself
        int index = CollectionUtils.indexOf(program, 0, ix -> ix instanceof LabelInstruction l && l.getLabel().equals(functionLabel));
        program.add(index + 1, processor.createOp(program.get(index).getAstContext(), Operation.ADD,
                function.getFnStackFrame(), function.getFnStackFrame(), LogicNumber.create(stack.frameSize())));
    }
}
