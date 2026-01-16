package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.mindcode.logic.arguments.GenericArgument;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.mindcode.logic.opcodes.TypedArgument;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
public class BaseInstructionProcessorTest extends AbstractCodeGeneratorTest {

    // NOTE: instruction validation is turned off for unit tests
    // If it gets turned on again, activate these tests

    //@Test
    void rejectsIncompatibleInstructions() {
        Assertions.assertThrows(MindcodeInternalError.class, () ->
                createInstruction(UCONTROL, "pathfind")
        );
    }

    //@Test
    void rejectsWrongNumberOfArguments() {
        Assertions.assertThrows(MindcodeInternalError.class, () ->
                createInstruction(PRINT, "a", "b")
        );
    }

    //@Test
    void rejectsInvalidArgumentsOnly() {
        Assertions.assertDoesNotThrow(() ->
                createInstruction(URADAR, "flying", "enemy", "boss", "health", "0", "MIN_MAX", "result")
        );

        Assertions.assertDoesNotThrow(() ->
                createInstruction(ULOCATE, "building", "core", "0", "@copper", "outx", "outy", "found", "building")
        );

        Assertions.assertThrows(MindcodeInternalError.class, () ->
                createInstruction(DRAW, "fluffyBunny", "0", "0")
        );

        Assertions.assertThrows(MindcodeInternalError.class, () ->
                createInstruction(URADAR, "flying", "enemy", "fluffyBunny", "health", "0", "MIN_MAX", "result")
        );

        Assertions.assertThrows(MindcodeInternalError.class, () ->
                createInstruction(URADAR, "flying", "enemy", "boss", "fluffyBunny", "0", "MIN_MAX", "result")
        );

        Assertions.assertThrows(MindcodeInternalError.class, () ->
                createInstruction(ULOCATE, "building", "fluffyBunny", "0", "@copper", "outx", "outy", "found", "building")
        );
    }

    @Test
    void replacesAllArguments() {
        LogicInstruction original = createInstruction(DRAW, "clear", "0", "0", "0");
        LogicInstruction replaced = ip.replaceAllArgs(original, new GenericArgument("0"), new GenericArgument("255"));
        Assertions.assertEquals(
                createInstruction(DRAW, "clear", "255", "255", "255"),
                replaced
        );
    }

    @Test
    void providesCorrectMetadata_ArgumentTypes() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        Assertions.assertEquals(
                List.of(
                        new TypedArgument(InstructionParameterType.LOCATE, new GenericArgument("ore")),
                        new TypedArgument(InstructionParameterType.UNUSED, new GenericArgument("core")),
                        new TypedArgument(InstructionParameterType.UNUSED, new GenericArgument("true")),
                        new TypedArgument(InstructionParameterType.ORE, new GenericArgument("@lead")),
                        new TypedArgument(InstructionParameterType.OUTPUT, new GenericArgument("outx")),
                        new TypedArgument(InstructionParameterType.OUTPUT, new GenericArgument("outy")),
                        new TypedArgument(InstructionParameterType.RESULT, new GenericArgument("found")),
                        new TypedArgument(InstructionParameterType.UNUSED_OUTPUT, new GenericArgument("building"))
                ),
                ix.getTypedArguments()
        );
    }

    @Test
    public void testAvoidsPrecisionLoss() {
        List.of("0", "1", "123456.789", "1e10", "1e25", "123456E25", "3333333e-40", "33333333333e-20").forEach(number -> {
            List<MindcodeMessage> messages = new ArrayList<>();
            InstructionProcessor processor = InstructionProcessorFactory.getInstructionProcessor(messages::add,
                    ProcessorVersion.V8A, ProcessorType.W, nameCreator);
            processor.mlogRewrite(SourcePosition.EMPTY, number, true);
            Assertions.assertEquals(List.of(), messages);
        });
    }

    @Test
    public void testHasPrecisionLoss() {
        List.of("33333333e-40", "99999999e-40", "3333333333e-40", "7777777777e10").forEach(number -> {
            List<MindcodeMessage> messages = new ArrayList<>();
            InstructionProcessor processor = InstructionProcessorFactory.getInstructionProcessor(messages::add,
                    ProcessorVersion.V7A, ProcessorType.W, nameCreator);
            processor.mlogRewrite(SourcePosition.EMPTY, number, true);
            if (messages.isEmpty()) {
                Assertions.fail("No precision loss for number " + number);
            }
        });
    }

    @Test
    public void testLongParse() {
        List.of("0", "+0", "-0", "235235", "99424", "1234", "1", "-24242", "170589", "-289157", "4246", "19284",
                        "-672396", "-42412042040945", "1592835012852095")
                .forEach(this::checkLong);
    }

    @Test
    public void testDoubleParse() {
        List.of("3.0", "0", "0.0", "123.456", "123", "145.6", "1e10", "-512515", "-535.646", "999.9344", "0.24324",
                        ".325235", "3424324.", "+.31245", "-.51354", ".0", "-.0", "+.0", "0.000002", "200000.2000",
                        "2000.00004", "-0.5", "1e10", "1e25", "33333E32", "333333333333333e-40")
                .forEach(this::checkDouble);
    }

    private void checkDouble(String value) {
        Assertions.assertEquals(Double.parseDouble(value),
                Double.parseDouble(ip.mlogRewrite(SourcePosition.EMPTY, value, true).orElse("NaN")), 0.00001);
    }

    private void checkLong(String value) {
        Assertions.assertEquals(Long.parseLong(value),
                Long.parseLong(ip.mlogRewrite(SourcePosition.EMPTY, value, true).orElse("NaN")));
    }

    //@Test
    public void printLinkedBlockNames() {
        BlockType.getBaseLinkNames(ip.getMetadata()).stream().sorted().distinct().forEach(System.out::println);
    }

    //@Test
    public void printLinkedBlockNamesN() {
        BlockType.getBaseLinkNames(ip.getMetadata()).stream().sorted().distinct()
                .forEach(name -> IntStream.range(1, 10).forEach(i -> System.out.println(name + i)));
    }
}
