package info.teksol.mc.mindcode.logic.arguments.arrays;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.ArrayConstruction;
import info.teksol.mc.mindcode.logic.instructions.ReadArrInstruction;
import info.teksol.mc.mindcode.logic.instructions.WriteArrInstruction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class ArrayConstructorFactory {
    private static @Nullable ArrayConstructorContext context = null;

    private static ArrayConstructorContext context() {
        if (MindcodeCompiler.initialized()) return MindcodeCompiler.getContext();
        return Objects.requireNonNull(context);
    }

    public static void setContext(@Nullable ArrayConstructorContext context) {
        ArrayConstructorFactory.context = context;
    }

    public static ArrayConstructor create(ArrayAccessInstruction instruction) {
        ArrayConstructorContext context = context();
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
