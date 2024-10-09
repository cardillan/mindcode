package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.mindcode.mimex.Icons;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConstantExpressionEvaluatorTest extends AbstractGeneratorTest {

    @Test
    void removesConstantsFromCode() {
        assertCompilesTo("""
                        const VALUE = 100;
                        print(VALUE);
                        """,
                createInstruction(PRINT, "100"),
                createInstruction(END)
        );
    }

    @Test
    void removesConstantsInPrintFmt() {
        assertCompilesTo("""
                        const VALUE = 100;
                        print($"Value: $VALUE");
                        """,
                createInstruction(PRINT, q("Value: ")),
                createInstruction(PRINT, "100"),
                createInstruction(END)
        );
    }

    @Test
    void evaluatesExpressionsWithBinaryLiterals() {
        assertCompilesTo("""
                        print(0b0011 + 0b1100);
                        """,
                createInstruction(PRINT, "15"),
                createInstruction(END)
        );
    }

    @Test
    void acceptsStringConstants() {
        assertCompilesTo("""
                        const TEXT = "Hello";
                        print(TEXT);
                        """,
                createInstruction(PRINT, q("Hello")),
                createInstruction(END)
        );
    }

    @Test
    void refusesVariableBasedConstant() {
        assertThrows(MindcodeException.class,
                () -> generateInstructions("a = 10;  const A = a;"));
    }

    @Test
    void refusesNondeterministicConstant() {
        assertThrows(MindcodeException.class,
                () -> generateInstructions("const A = rand(10);"));
    }

    @Test
    void refusesFunctionBasedConstant() {
        assertThrows(MindcodeException.class,
                () -> generateInstructions("""
                        def foo() 5; end;
                        const A = foo();
                        """
                )
        );
    }

    @Test
    void refusesMlogIncompatibleConstants() {
        // TODO Should work in 8A
        assertThrows(MindcodeException.class,
                () -> generateInstructions(
                        createTestCompiler(createCompilerProfile().setProcessorVersion(ProcessorVersion.V7A)),
                        "const A = 10**40;"));
    }

    @Test
    void evaluatesStringConcatenation() {
        assertCompilesTo("""
                        a = "A" + "B";
                        print(a);
                        """,
                createInstruction(SET, "a", q("AB")),
                createInstruction(PRINT, "a"),
                createInstruction(END)
        );
    }

    @Test
    void evaluatesStringConcatenationWithConstant() {
        assertCompilesTo("""
                        const A = "A";
                        a = A + "B";
                        print(a);
                        """,
                createInstruction(SET, "a", q("AB")),
                createInstruction(PRINT, "a"),
                createInstruction(END)
        );
    }

    @Test
    void evaluatesStringConcatenationWithIcon() {
        assertCompilesTo("""
                        a = "[]" + ITEM_COAL;
                        print(a);
                        """,
                createInstruction(SET, "a", q("[]" + Icons.getIconValue("ITEM_COAL").format())),
                createInstruction(PRINT, "a"),
                createInstruction(END)
        );
    }

    @Test
    void evaluatesStringConcatenationWithNumber() {
        assertCompilesTo("""
                        const COUNT = 10;
                        a = "Total: " + COUNT;
                        print(a);
                        """,
                createInstruction(SET, "a", q("Total: 10")),
                createInstruction(PRINT, "a"),
                createInstruction(END)
        );
    }

    @Test
    void evaluatesBooleanConcatenationWithString() {
        assertCompilesTo("""
                        const TRUTH = 1 < 10;
                        a = TRUTH + " is true";
                        print(a);
                        """,
                createInstruction(SET, "a", q("1 is true")),
                createInstruction(PRINT, "a"),
                createInstruction(END)
        );
    }

    @Test
    void refusesInvalidStringOperation() {
        assertThrows(MindcodeException.class,
                () -> generateInstructions("""
                        a = "A" - "B";
                        print(a);
                        """
                )
        );
    }

    @Test
    void refusesPartialStringConcatenation() {
        assertThrows(MindcodeException.class,
                () -> generateInstructions("""
                        a = "A" + B;
                        print(a);
                        """
                )
        );
    }

    @Test
    void refusesStringFunction() {
        assertThrows(MindcodeException.class,
                () -> generateInstructions("""
                        a = max("A", "B");
                        print(a);
                        """
                )
        );
    }

    @Test
    void refusesPartialStringFunction() {
        assertThrows(MindcodeException.class,
                () -> generateInstructions("""
                        a = max("A", 0);
                        print(a);
                        """
                )
        );
    }

    @Test
    void refusesUnaryStringFunction() {
        assertThrows(MindcodeException.class,
                () -> generateInstructions("""
                        a = not "A";
                        print(a);
                        """
                )
        );
    }
}
