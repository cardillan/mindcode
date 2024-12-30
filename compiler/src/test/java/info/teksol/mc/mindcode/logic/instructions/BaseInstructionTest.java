package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.AbstractTestBase;
import info.teksol.mc.mindcode.compiler.CompilationPhase;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.ULOCATE;

@NullMarked
class BaseInstructionTest extends AbstractTestBase {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.COMPILER;
    }

    @Test
    void providesCorrectMetadata_TotalInputs() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        Assertions.assertEquals(1, ix.getInputs());
    }

    @Test
    void providesCorrectMetadata_TotalOutputs() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        Assertions.assertEquals(4, ix.getOutputs());
    }

    @Test
    void providesCorrectMetadata_InputValues() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        Assertions.assertEquals(List.of(_logic("@lead")), ix.inputArgumentsStream().toList());
    }

    @Test
    void providesCorrectMetadata_OutputValues() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        Assertions.assertEquals(_logic("outx", "outy", "found", "building"), ix.outputArgumentsStream().toList());
    }
}