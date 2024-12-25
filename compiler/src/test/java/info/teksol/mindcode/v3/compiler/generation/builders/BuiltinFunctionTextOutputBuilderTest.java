package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.v3.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class BuiltinFunctionTextOutputBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class Formattables {
        @Test
        void compilesEnhancedComment() {
            assertCompiles("""
                            /// Iteration $i, value ${a + b}.
                            """,
                    createInstruction(OP, "add", var(0), "a", "b"),
                    createInstruction(REMARK, q(" Iteration ")),
                    createInstruction(REMARK, "i"),
                    createInstruction(REMARK, q(",")),
                    createInstruction(REMARK, q(" value ")),
                    createInstruction(REMARK, var(0)),
                    createInstruction(REMARK, q("."))
            );
        }

        @Test
        void compilesSimpleFormattable() {
            assertCompiles("""
                            print($"Value: $value.");
                            """,
                    createInstruction(PRINT, q("Value: ")),
                    createInstruction(PRINT, "value"),
                    createInstruction(PRINT, q("."))
            );
        }

        @Test
        void compilesInterpolatedFormattable() {
            assertCompiles("""
                            println($"Value: ${x + y}.");
                            """,
                    createInstruction(OP, "add", var(0), "x", "y"),
                    createInstruction(PRINT, q("Value: ")),
                    createInstruction(PRINT, var(0)),
                    createInstruction(PRINT, q(".")),
                    createInstruction(PRINT, q("\n"))
            );
        }

        @Test
        void compilesFormattablePlaceholder() {
            assertCompiles("""
                            remark($"Value: $.", a * b);
                            """,
                    createInstruction(OP, "mul", var(0), "a", "b"),
                    createInstruction(REMARK, q("Value: ")),
                    createInstruction(REMARK, var(0)),
                    createInstruction(REMARK, q("."))
            );
        }

        @Test
        void compilesFormattableAsConstant() {
            assertCompiles("""
                            const fmt = $"Value: ${x + y}.";
                            print(fmt);
                            """,
                    createInstruction(OP, "add", var(0), "x", "y"),
                    createInstruction(PRINT, q("Value: ")),
                    createInstruction(PRINT, var(0)),
                    createInstruction(PRINT, q("."))
            );
        }
    }

    @Nested
    class Errors {
        @Test
        void refusesTooFewValues() {
            assertGeneratesMessages(
                    expectedMessages().add("Not enough arguments for formattable placeholders."),
                    """
                            print($"First: $, second: $.", first);
                            """);
        }

        @Test
        void refusesTooManyValues() {
            assertGeneratesMessages(
                    expectedMessages().add("Too many arguments for formattable placeholders."),
                    """
                            print($"First: $, second: $.", first, second, third);
                            """);
        }

        @Test
        void refusesSecondFormattable() {
            assertGeneratesMessages(
                    expectedMessages().add("A formattable string literal can only be used as a first argument to the print or remark function."),
                    """
                            print($"Hello, $", $"Hello $name");
                            """);
        }
    }

}