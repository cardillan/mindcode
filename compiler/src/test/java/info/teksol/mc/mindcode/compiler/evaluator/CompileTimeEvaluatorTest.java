package info.teksol.mc.mindcode.compiler.evaluator;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class CompileTimeEvaluatorTest extends AbstractCodeGeneratorTest {

    @Test
    void removesConstantsFromCode() {
        assertCompilesTo("""
                        const VALUE = 100;
                        print(VALUE);
                        """,
                createInstruction(PRINT, "100")
        );
    }

    @Test
    void removesConstantsInPrintFmt() {
        assertCompilesTo("""
                        const VALUE = 100;
                        print($"Value: $VALUE");
                        """,
                createInstruction(PRINT, q("Value: ")),
                createInstruction(PRINT, "100")
        );
    }

    @Test
    void evaluatesExpressionsWithBinaryLiterals() {
        assertCompilesTo("""
                        print(0b0011 + 0b1100);
                        """,
                createInstruction(PRINT, "15")
        );
    }

    @Test
    void evaluatesExpressionsWithCharacterLiterals() {
        assertCompilesTo("""
                        print('Z' - 'A');
                        """,
                createInstruction(PRINT, "25")
        );
    }

    @Test
    void evaluatesEncode() {
        assertCompilesTo("""
                        const a = encode('A', 0, 1, 2, 3, @copper.@id, @graphite-press.@id, @mega.@id);
                        print(a);
                        """,
                createInstruction(PRINT, q("ABCDAAW"))
        );
    }


    @Test
    void evaluatesEncodeInFunction() {
        assertCompilesTo("""
                        def foo(type, a, b)
                            encode('A', type.@id, a + 10, b * 2) + " (" + type.@name + ")";
                        end;
                        
                        const a = foo(@lead, 2, 4);
                        print(a);
                        """,
                createInstruction(PRINT, q("BMI (lead)"))
        );
    }

    @Test
    void acceptsStringConstants() {
        assertCompilesTo("""
                        const TEXT = "Hello";
                        print(TEXT);
                        """,
                createInstruction(PRINT, q("Hello"))
        );
    }

    @Test
    void acceptsFunctionBasedConstant() {
        assertCompilesTo("""
                        def foo() 5; end;
                        const A = foo();
                        print(A);
                        """,
                createInstruction(PRINT, "5")
        );
    }

    @Test
    void refusesVariableBasedConstant() {
        assertGeneratesMessages(
                expectedMessages()
                        .add("Value assigned to constant 'A' is not a constant expression."),
                "a = 10; const A = a;"
        );
    }

    @Test
    void refusesNondeterministicConstant() {
        assertGeneratesMessages(
                expectedMessages()
                        .add("Value assigned to constant 'A' is not a constant expression."),
                "const A = rand(10);"
        );
    }

    @Test
    void refusesMlog7IncompatibleConstants() {
        assertGeneratesMessage(
                "Value '1.0E40' does not have a valid mlog representation.",
                "#set target = 7; const A = 10**40;"
        );
    }

    @Test
    void supportsLargeConstantsInMlog8() {
        assertGeneratesMessages(
                expectedMessages(),
                """
                        #set target = 8;
                        const A = 10**40;"""
        );
    }

    @Test
    void supportColorLiterals() {
        assertCompilesTo("""
                        const VALUE = %00112233;
                        print(2 * VALUE);
                        """,
                createInstruction(PRINT, "%00224466")
        );
    }


    @Test
    void evaluatesStringConcatenation() {
        assertCompilesTo("""
                        a = "A" + "B";
                        print(a);
                        """,
                createInstruction(SET, "a", q("AB")),
                createInstruction(PRINT, "a")
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
                createInstruction(PRINT, "a")
        );
    }

    @Test
    void evaluatesStringConcatenationWithIcon() {
        assertCompilesTo("""
                        a = "[]" + ITEM_COAL;
                        print(a);
                        """,
                createInstruction(SET, "a", q("[]" + ip.getMetadata().getIcons().getIconValue("ITEM_COAL").format(null))),
                createInstruction(PRINT, "a")
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
                createInstruction(PRINT, "a")
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
                createInstruction(PRINT, "a")
        );
    }

    @Test
    void refusesInvalidStringOperation() {
        assertGeneratesMessage(
                "Unsupported string expression.",
                """
                        a = "A" - "B";
                        print(a);
                        """
        );
    }

    @Test
    void refusesPartialStringConcatenation() {
        assertGeneratesMessage(
                "Unsupported string expression.",
                """
                        a = "A" + B;
                        print(a);
                        """
        );
    }

    @Test
    void refusesStringFunction() {
        assertGeneratesMessage(
                "Unsupported string expression.",
                """
                        a = max("A", "B");
                        print(a);
                        """
        );
    }

    @Test
    void refusesAsinAcosAtanInVersion6() {
        assertGeneratesMessages(expectedMessages()
                        .add("Unknown function 'asin'.")
                        .add("Unknown function 'acos'.")
                        .add("Unknown function 'atan'."),
                """
                        #set target = 6;
                        print(asin(0) + acos(0) + atan(0));
                        """
        );
    }

    @Test
    void refusesPartialStringFunction() {
        assertGeneratesMessage(
                "Unsupported string expression.",
                """
                        a = max("A", 0);
                        print(a);
                        """
        );
    }

    @Test
    void refusesUnaryStringFunction() {
        assertGeneratesMessage(
                "Unsupported string expression.",
                """
                        a = not "A";
                        print(a);
                        """
        );
    }

    @Test
    void resolvesConstantExpressions() {
        assertCompilesTo("""
                        a = 1 + 2;
                        b = 1 / 0;
                        c = min(3, 4);
                        d = 1 < 2 ? b : c;
                        e = abs(-3);
                        f = ~1;
                        g = not false;
                        h = -(2 + 3);
                        i = 1 / 10000;
                        j = 0 === null;
                        """,
                createInstruction(SET, "a", "3"),
                createInstruction(SET, "b", "null"),
                createInstruction(SET, "c", "3"),
                createInstruction(SET, "d", "b"),
                createInstruction(SET, "e", "3"),
                createInstruction(SET, "f", "-2"),
                createInstruction(SET, "g", "true"),
                createInstruction(SET, "h", "-5"),
                createInstruction(SET, "i", "0.0001"),
                createInstruction(SET, "j", "false")
        );
    }

    @Test
    void resolvesConstantExpressionsVersion7() {
        assertCompilesTo("""
                        #set target = 7;
                        print(log10(10 ** 50));
                        """,
                createInstruction(PRINT, "50")
        );
    }

    @Test
    void ignoresUnrepresentableValues() {
        assertCompilesTo("""
                        print(1 << 63);
                        """,
                createInstruction(OP, "shl", tmp(0), "1", "63"),
                createInstruction(PRINT, tmp(0))
        );
    }
}
