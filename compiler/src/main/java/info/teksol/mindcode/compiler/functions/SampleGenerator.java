package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.MlogInstruction;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.OpcodeVariant;

import java.util.function.Consumer;

interface SampleGenerator {
    String getName();

    String decompile(MlogInstruction instruction);

    OpcodeVariant getOpcodeVariant();

    String generateSampleCall();

    LogicInstruction generateSampleInstruction();

    default Opcode getOpcode() {
        return getOpcodeVariant().opcode();
    }

    default String getNote() {
        return "";
    }

    default String generateSecondarySampleCall() {
        return null;
    }

    default void register(Consumer<SampleGenerator> registry) {
        registry.accept(this);
    }
}
