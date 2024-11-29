package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.mindcode.mimex.Icons;
import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

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
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add("Value assigned to constant 'A' is not a constant expression.")
                        .add("Constant declaration of 'A' does not use a constant expression."),
                "a = 10; const A = a;"
        );
    }

    @Test
    void refusesNondeterministicConstant() {
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add("Value assigned to constant 'A' is not a constant expression.")
                        .add("Constant declaration of 'A' does not use a constant expression."),
                "const A = rand(10);"
        );
    }

    @Test
    void refusesFunctionBasedConstant() {
        assertGeneratesMessages(
                ExpectedMessages.create()
                        .add("Value assigned to constant 'A' is not a constant expression.")
                        .add("Constant declaration of 'A' does not use a constant expression."),
                """
                        def foo() 5; end;
                        const A = foo();
                        """
        );
    }

    @Test
    void refusesMlog7IncompatibleConstants() {
        assertGeneratesMessages(
                createTestCompiler(createCompilerProfile().setProcessorVersion(ProcessorVersion.V7A)),
                ExpectedMessages.create()
                        .add("Value assigned to constant 'A' (1.0E40) doesn't have a valid mlog representation.")
                        .add("Constant declaration of 'A' does not use a constant expression."),
                "const A = 10**40;"
        );
    }

    @Test
    void supportsLargeConstantsInMlog8() {
        assertGeneratesMessages(
                createTestCompiler(createCompilerProfile().setProcessorVersion(ProcessorVersion.V8A)),
                ExpectedMessages.none(),
                "const A = 10**40;"
        );
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
                createInstruction(SET, "a", q("[]" + Icons.getIconValue("ITEM_COAL").format(null))),
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
        assertGeneratesMessages(
                ExpectedMessages.create().add("Unsupported string expression."),
                """
                        a = "A" - "B";
                        print(a);
                        """
        );
    }

    @Test
    void refusesPartialStringConcatenation() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Unsupported string expression."),
                """
                        a = "A" + B;
                        print(a);
                        """
        );
    }

    @Test
    void refusesStringFunction() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Unsupported string expression."),
                """
                       a = max("A", "B");
                       print(a);
                       """
        );
    }

    @Test
    void refusesPartialStringFunction() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Unsupported string expression."),
                """
                       a = max("A", 0);
                       print(a);
                       """
        );
    }

    @Test
    void refusesUnaryStringFunction() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Unsupported string expression."),
                """
                       a = not "A";
                       print(a);
                       """
        );
    }
}
