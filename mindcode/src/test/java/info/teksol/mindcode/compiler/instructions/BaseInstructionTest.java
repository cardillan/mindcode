package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.ULOCATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Order(99)
class BaseInstructionTest extends AbstractGeneratorTest {

    @Test
    void providesCorrectMetadata_TotalInputs() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        assertEquals(1, ix.getInputs());
    }

    @Test
    void providesCorrectMetadata_TotalOutputs() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        assertEquals(4, ix.getOutputs());
    }

    @Test
    void providesCorrectMetadata_InputValues() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        assertEquals(List.of(_logic("@lead")), ix.inputArgumentsStream().toList());
    }

    @Test
    void providesCorrectMetadata_OutputValues() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        assertEquals(_logic("outx", "outy", "found", "building"), ix.outputArgumentsStream().toList());
    }
}