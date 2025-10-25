package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.postprocess.JumpTable;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

@NullMarked
public class SingleElementArrayConstructor extends TablelessArrayConstructor {
    public SingleElementArrayConstructor(ArrayConstructorContext context, ArrayAccessInstruction instruction) {
        super(context, instruction);
    }

    @Override
    public SideEffects createSideEffects() {
        return switch (accessType) {
            case READ -> SideEffects.reads(arrayElements());
            case WRITE -> SideEffects.writes(arrayElements());
        };
    }

    @Override
    public int getInstructionSize(@Nullable Map<String, Integer> sharedStructures) {
        return boundsCheckSize() + 1;
    }

    @Override
    public double getExecutionSteps() {
        return boundsCheckExecutionSteps() + 1;
    }

    @Override
    public void expandInstruction(Consumer<LogicInstruction> consumer, Map<String, JumpTable> jumpTables) {
        AstContext astContext = instruction.getAstContext();
        generateBoundsCheck(astContext, consumer, instruction.getIndex(), 1 );

        LocalContextfulInstructionsCreator creator = new LocalContextfulInstructionsCreator(processor, astContext, consumer);
        ValueStore element = arrayStore.getElements().getFirst();
        switch (instruction) {
            case ReadArrInstruction rix -> element.readValue(creator, rix.getResult());
            case WriteArrInstruction wix -> element.setValue(creator, wix.getValue());
            default -> throw new MindcodeInternalError("Unhandled ArrayAccessInstruction");
        }
    }
}
