package info.teksol.mindcode.mindustry.instructions;

import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.generator.GenerationException;
import info.teksol.mindcode.mindustry.logic.ArgumentType;
import info.teksol.mindcode.mindustry.logic.TypedArgument;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseInstructionProcessorTest extends AbstractGeneratorTest {

    @Test
    void rejectsIncompatibleInstructions() {
        assertThrows(GenerationException.class, () ->
                createInstruction(WAIT, "50")
        );
    }

    @Test
    void rejectsWrongNumberOfArguments() {
        assertThrows(GenerationException.class, () ->
                createInstruction(PRINT, "a", "b")
        );
    }

    @Test
    void rejectsInvalidArgumentsOnly() {
        assertThrows(GenerationException.class, () ->
                createInstruction(DRAW, "fluffyBunny", "0", "0")
        );

        assertDoesNotThrow(() ->
                createInstruction(URADAR, "flying", "enemy", "boss", "health", "0", "MIN_MAX", "result")
        );

        assertThrows(GenerationException.class, () ->
                createInstruction(URADAR, "flying", "enemy", "fluffyBunny", "health", "0", "MIN_MAX", "result")
        );

        assertThrows(GenerationException.class, () ->
                createInstruction(URADAR, "flying", "enemy", "boss", "fluffyBunny", "0", "MIN_MAX", "result")
        );

        assertDoesNotThrow(() ->
                createInstruction(ULOCATE, "building", "core", "0", "@copper", "outx", "outy", "found", "building")
        );

        assertThrows(GenerationException.class, () ->
                createInstruction(ULOCATE, "building", "fluffyBunny", "0", "@copper", "outx", "outy", "found", "building")
        );
    }

    @Test
    void replacesInstructionArgument() {
        LogicInstruction original = createInstruction(DRAW, "clear", "0", "0", "255");
        LogicInstruction replaced = getInstructionProcessor().replaceArg(original, 1, "255");
        assertEquals(
                createInstruction(DRAW, "clear", "255", "0", "255"),
                replaced
        );
    }

    @Test
    void keepsInstructionIfArgumentIdentical() {
        LogicInstruction original = createInstruction(DRAW, "clear", "0", "0", "255");
        LogicInstruction replaced = getInstructionProcessor().replaceArg(original, 1, "0");
        assertTrue(original == replaced);
    }

    @Test
    void replacesAllArguments() {
        LogicInstruction original = createInstruction(DRAW, "clear", "0", "0", "0");
        LogicInstruction replaced = getInstructionProcessor().replaceAllArgs(original, "0", "255");
        assertEquals(
                createInstruction(DRAW, "clear", "255", "255", "255"),
                replaced
        );
    }

    @Test
    void providesCorrectMetadata_TotalInputs() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        assertEquals(1, getInstructionProcessor().getTotalInputs(ix));
    }

    @Test
    void providesCorrectMetadata_TotalOutputs() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        assertEquals(4, getInstructionProcessor().getTotalOutputs(ix));
    }

    @Test
    void providesCorrectMetadata_InputValues() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        assertEquals(List.of("@lead"), getInstructionProcessor().getInputValues(ix));
    }

    @Test
    void providesCorrectMetadata_OutputValues() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        assertEquals(List.of("outx", "outy", "found", "building"), getInstructionProcessor().getOutputValues(ix));
    }

    @Test
    void providesCorrectMetadata_ArgumentTypes() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        assertEquals(
                List.of(
                        new TypedArgument(ArgumentType.LOCATE, "ore"),
                        new TypedArgument(ArgumentType.UNUSED, "core"),
                        new TypedArgument(ArgumentType.UNUSED, "true"),
                        new TypedArgument(ArgumentType.ORE, "@lead"),
                        new TypedArgument(ArgumentType.OUTPUT, "outx"),
                        new TypedArgument(ArgumentType.OUTPUT, "outy"),
                        new TypedArgument(ArgumentType.RESULT, "found"),
                        new TypedArgument(ArgumentType.UNUSED_OUTPUT, "building")
                ),
                getInstructionProcessor().getTypedArguments(ix).collect(Collectors.toUnmodifiableList())
        );
    }
}
