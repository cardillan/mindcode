package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

/// Tracks heap usage. Creates heap variables and assigns indexes to them.
@NullMarked
public class HeapTracker extends AbstractMessageEmitter {
    private final InstructionProcessor processor;
    private final LogicVariable heapMemory;
    private int currentHeapIndex;
    private final int endHeapIndex;

    private HeapTracker(CodeGeneratorContext context, LogicVariable heapMemory, int startHeapIndex, int endHeapIndex) {
        super(context.messageConsumer());
        this.processor = context.instructionProcessor();
        this.heapMemory = heapMemory;
        this.currentHeapIndex = startHeapIndex;
        this.endHeapIndex = endHeapIndex;
    }

    public static HeapTracker createDefaultTracker(CodeGeneratorContext context) {
        return new HeapTracker(context, LogicVariable.INVALID, 0, Integer.MAX_VALUE);
    }

    public static HeapTracker createTracker(CodeGeneratorContext context, LogicVariable heapMemory, int startHeapIndex, int endHeapIndex) {
        return new HeapTracker(context, heapMemory, startHeapIndex, endHeapIndex);
    }

    public ExternalVariable createVariable(AstIdentifier identifier) {
        if (currentHeapIndex >= endHeapIndex) {
            error(identifier, "Not enough capacity in allocated heap for '%s'.", identifier.getName());
        }
        return new ExternalVariable(heapMemory, LogicNumber.get(currentHeapIndex++), processor.nextTemp());
    }
}