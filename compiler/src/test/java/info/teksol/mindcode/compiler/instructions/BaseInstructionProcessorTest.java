package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.generator.GenerationException;
import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.TypedArgument;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.*;

public class BaseInstructionProcessorTest extends AbstractGeneratorTest {

    @Test
    void rejectsIncompatibleInstructions() {
        assertThrows(GenerationException.class, () ->
                createInstruction(UCONTROL, "pathfind")
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
        assertSame(original, replaced);
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
                getInstructionProcessor().getTypedArguments(ix).toList()
        );
    }

    @Test
    public void testAvoidsPrecisionLoss() {
        List.of("0", "1", "123456.789", "1e10", "1e25", "123456E25", "3333333e-40", "33333333333e-20").forEach(number -> {
            messages.clear();
            instructionProcessor.mlogRewrite(number);
            assertEquals(List.of(), messages);
        });
    }

    @Test
    public void testHasPrecisionLoss() {
        List.of("33333333e-40", "99999999e-40", "3333333333e-40", "7777777777e10").forEach(number -> {
            messages.clear();
            instructionProcessor.mlogRewrite(number);
            if (messages.isEmpty()) {
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
                Double.parseDouble(instructionProcessor.mlogRewrite(value).orElse("NaN")), 0.00001);
    }

    private void checkLong(String value) {
        assertEquals(Long.parseLong(value),
                Long.parseLong(instructionProcessor.mlogRewrite(value).orElse("NaN")));
    }

    // Parsing code lifted from Mindustry itself (v143)

    private static long parseLong(String s, int radix, int start, int end, long defaultValue) {
        boolean negative = false;
        int i = start, len = end - start;
        long limit = -9223372036854775807L;
        if (len <= 0) {
            return defaultValue;
        } else {
            char firstChar = s.charAt(i);
            if (firstChar < '0') {
                if (firstChar == '-') {
                    negative = true;
                    limit = -9223372036854775808L;
                } else if (firstChar != '+') {
                    return defaultValue;
                }

                if (len == 1) return defaultValue;

                ++i;
            }

            long result;
            int digit;
            for (result = 0L; i < end; result -= digit) {
                digit = Character.digit(s.charAt(i++), radix);
                if (digit < 0) {
                    return defaultValue;
                }

                result *= radix;
                if (result < limit + (long) digit) {
                    return defaultValue;
                }
            }

            return negative ? result : -result;
        }
    }

    private static double parseDouble(String value, double defaultValue) {
        int len = value.length();
        if (len == 0) return defaultValue;

        int sign = 1;
        int start = 0, end = len;
        char last = value.charAt(len - 1), first = value.charAt(0);
        if (last == 'F' || last == 'f' || last == '.') {
            end--;
        }
        if (first == '+') {
            start = 1;
        }
        if (first == '-') {
            start = 1;
            sign = -1;
        }

        int dot = -1, e = -1;
        for (int i = start; i < end; i++) {
            char c = value.charAt(i);
            if (c == '.') dot = i;
            if (c == 'e' || c == 'E') e = i;
        }

        if (dot != -1 && dot < end) {
            //negation as first character
            long whole = start == dot ? 0 : parseLong(value, 10, start, dot, Long.MIN_VALUE);
            if (whole == Long.MIN_VALUE) return defaultValue;
            long dec = parseLong(value, 10, dot + 1, end, Long.MIN_VALUE);
            if (dec < 0) return defaultValue;
            return (whole + Math.copySign(dec / Math.pow(10, (end - dot - 1)), whole)) * sign;
        }

        //check scientific notation
        if (e != -1) {
            long whole = parseLong(value, 10, start, e, Long.MIN_VALUE);
            if (whole == Long.MIN_VALUE) return defaultValue;
            long power = parseLong(value, 10, e + 1, end, Long.MIN_VALUE);
            if (power == Long.MIN_VALUE) return defaultValue;
            return whole * pow(10, power) * sign;
        }

        //parse as standard integer
        long out = parseLong(value, 10, start, end, Long.MIN_VALUE);
        return out == Long.MIN_VALUE ? defaultValue : out * sign;
    }

    private static float pow(float a, float b) {
        return (float) Math.pow(a, b);
    }

}
