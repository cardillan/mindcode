package info.teksol.mindcode.v3.compiler.functions;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.MlogInstruction;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.OpcodeVariant;
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
