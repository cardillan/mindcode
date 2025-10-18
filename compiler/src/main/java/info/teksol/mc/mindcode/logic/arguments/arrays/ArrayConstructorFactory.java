package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.ArrayConstruction;
import info.teksol.mc.mindcode.logic.instructions.ReadArrInstruction;
import info.teksol.mc.mindcode.logic.instructions.WriteArrInstruction;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ArrayConstructorFactory {

    public static ArrayConstructor create(ArrayAccessInstruction instruction) {
        boolean compact = instruction.getArrayConstruction() == ArrayConstruction.COMPACT;
        return switch (instruction.getArrayOrganization()) {
            case INTERNAL -> compact ? new CompactSharedArrayConstructor(instruction)
                    : instruction instanceof ReadArrInstruction rix ? new ReadSharedArrayConstructor(rix)
                    : instruction instanceof WriteArrInstruction wix ? new WriteSharedArrayConstructor(wix)
                    : invalidConstructor();

            case INLINED -> compact ? new CompactInlinedArrayConstructor(instruction)
                    : instruction instanceof ReadArrInstruction rix ? new ReadInlinedArrayConstructor(rix)
                    : instruction instanceof WriteArrInstruction wix ? new WriteInlinedArrayConstructor(wix)
                    : invalidConstructor();

            case SINGLE -> new SingleElementArrayConstructor(instruction);
            case SHORT -> compact ? new CompactShortArrayConstructor(instruction) : new RegularShortArrayConstructor(instruction);
            case LOOKUP -> new LookupArrayConstructor(instruction);
            case EXTERNAL -> new ExternalArrayConstructor(instruction);

            default -> throw new MindcodeInternalError("Unsupported array organization: " + instruction.getArrayOrganization());
        };
    }

    private static ArrayConstructor invalidConstructor() {
        throw new MindcodeInternalError("Invalid array constructor");
    }

    private ArrayConstructorFactory() {
    }
}
