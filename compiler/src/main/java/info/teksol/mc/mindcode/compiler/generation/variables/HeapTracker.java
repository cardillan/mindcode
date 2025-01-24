package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

/// Tracks heap usage. Creates heap variables and assigns indexes to them.
@NullMarked
public class HeapTracker extends AbstractMessageEmitter {
    private final InstructionProcessor processor;
    private final LogicVariable heapMemory;
    private final boolean heapAllocated;
    private int currentHeapIndex;
    private final int endHeapIndex;

    private HeapTracker(CodeGeneratorContext context, LogicVariable heapMemory, int startHeapIndex, int endHeapIndex) {
        super(context.messageConsumer());
        this.processor = context.instructionProcessor();
        this.heapMemory = heapMemory;
        this.currentHeapIndex = startHeapIndex;
        this.endHeapIndex = endHeapIndex;
        this.heapAllocated = true;
    }

    private HeapTracker(CodeGeneratorContext context) {
        super(context.messageConsumer());
        this.processor = context.instructionProcessor();
        this.heapMemory = LogicVariable.INVALID;
        this.currentHeapIndex = 0;
        this.endHeapIndex = Integer.MAX_VALUE;
        this.heapAllocated = false;
    }

    public static HeapTracker createDefaultTracker(CodeGeneratorContext context) {
        return new HeapTracker(context);
    }

    public static HeapTracker createTracker(CodeGeneratorContext context, LogicVariable heapMemory, int startHeapIndex, int endHeapIndex) {
        return new HeapTracker(context, heapMemory, startHeapIndex, endHeapIndex);
    }

    public ValueStore createVariable(AstIdentifier identifier, Set<Modifier> modifiers) {
        if (!heapAllocated) {
            error(identifier, ERR.EXTERNAL_MISSING_HEAP);
        }
        if (currentHeapIndex >= endHeapIndex) {
            error(identifier, ERR.EXTERNAL_HEAP_EXCEEDED, identifier.getName());
        }

        LogicValue index = LogicNumber.create(currentHeapIndex++);

        return modifiers.contains(Modifier.CACHED)
                ? new ExternalCachedVariable(identifier.sourcePosition(), heapMemory, index, LogicVariable.global(identifier))
                : new ExternalVariable(identifier.sourcePosition(), heapMemory, index, processor.nextTemp());
    }
}
