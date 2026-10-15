package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.ContextFactory;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.ArrayConstruction;
import info.teksol.mc.mindcode.logic.instructions.ReadArrInstruction;
import info.teksol.mc.mindcode.logic.instructions.WriteArrInstruction;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ArrayConstructorFactory {

    public static ArrayConstructor create(ArrayAccessInstruction instruction) {
        ArrayConstructorContext context = ContextFactory.getArrayConstructorContext();
        boolean compact = instruction.getArrayConstruction() == ArrayConstruction.COMPACT;
        return switch (instruction.getArrayOrganization()) {
            case INTERNAL -> compact ? new CompactSharedArrayConstructor(context, instruction)
                    : instruction instanceof ReadArrInstruction rix ? new ReadSharedArrayConstructor(context, rix)
                    : instruction instanceof WriteArrInstruction wix ? new WriteSharedArrayConstructor(context, wix)
                    : invalidConstructor();

            case INLINED -> compact ? new CompactInlinedArrayConstructor(context, instruction)
                    : instruction instanceof ReadArrInstruction rix ? new ReadInlinedArrayConstructor(context, rix)
                    : instruction instanceof WriteArrInstruction wix ? new WriteInlinedArrayConstructor(context, wix)
                    : invalidConstructor();

            case SINGLE -> new SingleElementArrayConstructor(context, instruction);
            case SHORT -> compact ? new CompactShortArrayConstructor(context, instruction) : new RegularShortArrayConstructor(context, instruction);
            case LOOKUP -> new LookupArrayConstructor(context, instruction);
            case EXTERNAL -> new ExternalArrayConstructor(context, instruction);

            default -> throw new MindcodeInternalError("Unsupported array organization: " + instruction.getArrayOrganization());
        };
    }

    private static ArrayConstructor invalidConstructor() {
        throw new MindcodeInternalError("Invalid array constructor");
    }

    private ArrayConstructorFactory() {
    }
}
