package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.logic.BaseArgument;
import info.teksol.mindcode.logic.InstructionParameterType;
import info.teksol.mindcode.logic.ParameterAssignment;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.mindcode.mimex.BlockType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.*;

public class BaseInstructionProcessorTest extends AbstractGeneratorTest {

    @Test
    void rejectsIncompatibleInstructions() {
        assertThrows(MindcodeInternalError.class, () ->
                createInstruction(UCONTROL, "pathfind")
        );
    }

    @Test
    void rejectsWrongNumberOfArguments() {
        assertThrows(MindcodeInternalError.class, () ->
                createInstruction(PRINT, "a", "b")
        );
    }

    @Test
    void rejectsInvalidArgumentsOnly() {
        // This is an internal error, as draw type is derived from function name
        // and won't be generated with invalid value from source code.
        assertThrows(MindcodeInternalError.class, () ->
                createInstruction(DRAW, "fluffyBunny", "0", "0")
        );

        assertDoesNotThrow(() ->
                createInstruction(URADAR, "flying", "enemy", "boss", "health", "0", "MIN_MAX", "result")
        );

        assertThrows(MindcodeException.class, () ->
                createInstruction(URADAR, "flying", "enemy", "fluffyBunny", "health", "0", "MIN_MAX", "result")
        );

        assertThrows(MindcodeException.class, () ->
                createInstruction(URADAR, "flying", "enemy", "boss", "fluffyBunny", "0", "MIN_MAX", "result")
        );

        assertDoesNotThrow(() ->
                createInstruction(ULOCATE, "building", "core", "0", "@copper", "outx", "outy", "found", "building")
        );

        assertThrows(MindcodeException.class, () ->
                createInstruction(ULOCATE, "building", "fluffyBunny", "0", "@copper", "outx", "outy", "found", "building")
        );
    }

    @Test
    void replacesAllArguments() {
        LogicInstruction original = createInstruction(DRAW, "clear", "0", "0", "0");
        LogicInstruction replaced = ip.replaceAllArgs(original, new BaseArgument("0"), new BaseArgument("255"));
        assertEquals(
                createInstruction(DRAW, "clear", "255", "255", "255"),
                replaced
        );
    }

    @Test
    void providesCorrectMetadata_ArgumentTypes() {
        LogicInstruction ix = createInstruction(ULOCATE, "ore", "core", "true", "@lead", "outx", "outy", "found", "building");
        assertEquals(
                List.of(
                        new ParameterAssignment(InstructionParameterType.LOCATE, new BaseArgument("ore")),
                        new ParameterAssignment(InstructionParameterType.UNUSED, new BaseArgument("core")),
                        new ParameterAssignment(InstructionParameterType.UNUSED, new BaseArgument("true")),
                        new ParameterAssignment(InstructionParameterType.ORE, new BaseArgument("@lead")),
                        new ParameterAssignment(InstructionParameterType.OUTPUT, new BaseArgument("outx")),
                        new ParameterAssignment(InstructionParameterType.OUTPUT, new BaseArgument("outy")),
                        new ParameterAssignment(InstructionParameterType.RESULT, new BaseArgument("found")),
                        new ParameterAssignment(InstructionParameterType.UNUSED_OUTPUT, new BaseArgument("building"))
                ),
                ix.getAssignments()
        );
    }

    @Test
    public void testAvoidsPrecisionLoss() {
        List.of("0", "1", "123456.789", "1e10", "1e25", "123456E25", "3333333e-40", "33333333333e-20").forEach(number -> {
            TestCompiler compiler = createTestCompiler();
            compiler.processor.mlogRewrite(number);
            assertEquals(List.of(), compiler.getMessages());
        });
    }

    @Test
    public void testHasPrecisionLoss() {
        List.of("33333333e-40", "99999999e-40", "3333333333e-40", "7777777777e10").forEach(number -> {
            TestCompiler compiler = createTestCompiler(createCompilerProfile().setProcessorVersion(ProcessorVersion.V7A));
            compiler.processor.mlogRewrite(number);
            if (compiler.getMessages().isEmpty()) {
                fail("No precision loss for number " + number);
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
        assertEquals(Double.parseDouble(value),
                Double.parseDouble(ip.mlogRewrite(value).orElse("NaN")), 0.00001);
    }

    private void checkLong(String value) {
        assertEquals(Long.parseLong(value),
                Long.parseLong(ip.mlogRewrite(value).orElse("NaN")));
    }

    //@Test
    public void printLinkedBlockNames() {
        BlockType.getBaseLinkNames().stream().sorted().forEach(System.out::println);
    }

    //@Test
    public void printLinkedBlockNamesN() {
        BlockType.getBaseLinkNames().stream().sorted()
                .forEach(name -> IntStream.range(1, 10).forEach(i -> System.out.println(name + i)));
    }
}
