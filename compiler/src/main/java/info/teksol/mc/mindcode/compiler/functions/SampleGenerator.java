package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.MlogInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.OpcodeVariant;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@NullMarked
interface SampleGenerator {
    String getName();

    @Nullable String decompile(MlogInstruction instruction);

    OpcodeVariant getOpcodeVariant();

    String generateSampleCall();

    LogicInstruction generateSampleInstruction();

    default Opcode getOpcode() {
        return getOpcodeVariant().opcode();
    }

    default String getNote() {
        return "";
    }

    default @Nullable String generateSecondarySampleCall() {
        return null;
    }

    default void register(Consumer<SampleGenerator> registry) {
        registry.accept(this);
    }
}
