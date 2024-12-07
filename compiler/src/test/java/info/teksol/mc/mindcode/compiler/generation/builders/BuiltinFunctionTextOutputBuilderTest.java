package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class BuiltinFunctionTextOutputBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class FormattableErrors {
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

    @Nested
    class FormattableOutput {
        @Test
        void compilesEnhancedComment() {
            assertCompilesTo("""
                            /// Iteration $i, value ${a + b}.
                            """,
                    createInstruction(OP, "add", var(0), ":a", ":b"),
                    createInstruction(REMARK, q("Iteration ")),
                    createInstruction(REMARK, ":i"),
                    createInstruction(REMARK, q(",")),
                    createInstruction(REMARK, q(" value ")),
                    createInstruction(REMARK, var(0)),
                    createInstruction(REMARK, q("."))
            );
        }

        @Test
        void compilesSimpleFormattable() {
            assertCompilesTo("""
                            print($"Values: $first$second$third.");
                            """,
                    createInstruction(PRINT, q("Values: ")),
                    createInstruction(PRINT, "first"),
                    createInstruction(PRINT, "second"),
                    createInstruction(PRINT, "third"),
                    createInstruction(PRINT, q("."))
            );
        }

        @Test
        void compilesInterpolatedFormattable() {
            assertCompilesTo("""
                            println($"Value: ${x + y}.");
                            """,
                    createInstruction(OP, "add", var(0), ":x", ":y"),
                    createInstruction(PRINT, q("Value: ")),
                    createInstruction(PRINT, var(0)),
                    createInstruction(PRINT, q(".")),
                    createInstruction(PRINT, q("\n"))
            );
        }

        @Test
        void compilesFormattablePlaceholder() {
            assertCompilesTo("""
                            remark($"Value: $.", a * b);
                            """,
                    createInstruction(OP, "mul", var(0), "a", "b"),
                    createInstruction(REMARK, q("Value: ")),
                    createInstruction(REMARK, var(0)),
                    createInstruction(REMARK, q("."))
            );
        }

        @Test
        void compilesTwoFormattablePlaceholders() {
            assertCompilesTo("""
                            remark($"Value: ${}$.", a, b);
                            """,
                    createInstruction(REMARK, q("Value: ")),
                    createInstruction(REMARK, "a"),
                    createInstruction(REMARK, "b"),
                    createInstruction(REMARK, q("."))
            );
        }

        @Test
        void compilesFormattableInsideFunction() {
            assertCompilesTo("""
                            void foo(x, y)
                                print($"Value: ${x + y}.");
                            end;
                            
                            foo(10, 20);
                            """,
                    createInstruction(SET, "__fn0_x", "10"),
                    createInstruction(SET, "__fn0_y", "20"),
                    createInstruction(OP, "add", var(0), "__fn0_x", "__fn0_y"),
                    createInstruction(PRINT, q("Value: ")),
                    createInstruction(PRINT, var(0)),
                    createInstruction(PRINT, q(".")),
                    createInstruction(LABEL, var(1000))

            );
        }

        @Test
        void compilesFormattableAsConstant() {
            assertCompilesTo("""
                            const fmt = $"Value: ${x + y}.";
                            print(fmt);
                            """,
                    createInstruction(OP, "add", var(0), "x", "y"),
                    createInstruction(PRINT, q("Value: ")),
                    createInstruction(PRINT, var(0)),
                    createInstruction(PRINT, q("."))
            );
        }

        @Test
        void compilesFormattableAsConstantInsideFunction() {
            assertCompilesTo("""
                            const fmt = $"Value: ${x + y}.";
                            void foo(x, y)
                                print(fmt);
                            end;
                            
                            foo(10, 20);
                            x = y = 5;
                            print(fmt);
                            """,
                    createInstruction(SET, "__fn0_x", "10"),
                    createInstruction(SET, "__fn0_y", "20"),
                    createInstruction(OP, "add", var(0), "__fn0_x", "__fn0_y"),
                    createInstruction(PRINT, q("Value: ")),
                    createInstruction(PRINT, var(0)),
                    createInstruction(PRINT, q(".")),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, "y", "5"),
                    createInstruction(SET, "x", "y"),
                    createInstruction(OP, "add", var(1), "x", "y"),
                    createInstruction(PRINT, q("Value: ")),
                    createInstruction(PRINT, var(1)),
                    createInstruction(PRINT, q("."))
            );
        }

        @Test
        void compilesFormattableConstantIndependentlyAtEachUsage() {
            assertCompilesTo("""
                            const fmt = $"Value: ${x + y}.";
                            const x = 1;
                            const y = 2;

                            void foo(x, y)
                                print(fmt);
                            end;
                            
                            print(fmt);
                            foo(10, 20);
                            """,
                    createInstruction(PRINT, q("Value: ")),
                    createInstruction(PRINT, "3"),
                    createInstruction(PRINT, q(".")),
                    createInstruction(SET, "__fn0_x", "10"),
                    createInstruction(SET, "__fn0_y", "20"),
                    createInstruction(OP, "add", var(0), "__fn0_x", "__fn0_y"),
                    createInstruction(PRINT, q("Value: ")),
                    createInstruction(PRINT, var(0)),
                    createInstruction(PRINT, q(".")),
                    createInstruction(LABEL, var(1000))
            );
        }
    }

    @Nested
    class PlainOutput {
        @Test
        void compilesEnhancedComment() {
            assertCompilesTo("""
                            /// This is an enhanced comment.
                            """,
                    createInstruction(REMARK, q("This is an enhanced comment."))
            );
        }

        @Test
        void compilesSingleValueOutput() {
            assertCompilesTo("""
                            print("Text");
                            """,
                    createInstruction(PRINT, q("Text"))
            );
        }

        @Test
        void compilesMultiValueOutput() {
            assertCompilesTo("""
                            x = println(a, b, c);
                            """,
                    createInstruction(PRINT, ":a"),
                    createInstruction(PRINT, ":b"),
                    createInstruction(PRINT, ":c"),
                    createInstruction(PRINT, q("\n")),
                    createInstruction(SET, ":x", ":c")
            );
        }

        @Test
        void compilesMultiValueRemark() {
            assertCompilesTo("""
                            remark(a, b, c);
                            """,
                    createInstruction(REMARK, "a"),
                    createInstruction(REMARK, "b"),
                    createInstruction(REMARK, "c")
            );
        }
    }

    @Nested
    class Printf {
        @Test
        void compilesPrintfWithVariableFormat() {
            assertCompilesTo("""
                            printf(format, name, value);
                            """,
                    createInstruction(PRINT, "format"),
                    createInstruction(FORMAT, "name"),
                    createInstruction(FORMAT, "value")
            );
        }

        @Test
        void generatesWarningAboutInefficientFormat() {
            assertCompilesTo(expectedMessages()
                            .add("The 'printf' function is called with a literal format string. Using 'print' or 'println' instead may produce better code."),
                    """
                            printf("Name: {1}, value: {2}\\n", name, value);
                            """,
                    createInstruction(PRINT, q("Name: {1}, value: {2}\n")),
                    createInstruction(FORMAT, ":name"),
                    createInstruction(FORMAT, ":value")
            );
        }

        @Test
        void generatesWarningAboutTooManyFormattablePlaceholders() {
            assertCompilesTo(expectedMessages()
                            .add("The 'printf' function is called with a literal format string. Using 'print' or 'println' instead may produce better code.")
                            .add("The 'printf' function doesn't have enough arguments for placeholders: 2 placeholder(s), 1 argument(s)."),
                    """
                            printf("Name: {1}, value: {2}\\n", name);
                            """,
                    createInstruction(PRINT, q("Name: {1}, value: {2}\n")),
                    createInstruction(FORMAT, ":name")
            );
        }

        @Test
        void generatesWarningAboutTooFewFormattablePlaceholders() {
            assertCompilesTo(expectedMessages()
                            .add("The 'printf' function is called with a literal format string. Using 'print' or 'println' instead may produce better code.")
                            .add("The 'printf' function has more arguments than placeholders: 2 placeholder(s), 3 argument(s)."),
                    """
                            printf("Name: {1}, value: {2}\\n", name, value, otherValue);
                            """,
                    createInstruction(PRINT, q("Name: {1}, value: {2}\n")),
                    createInstruction(FORMAT, ":name"),
                    createInstruction(FORMAT, ":value"),
                    createInstruction(FORMAT, ":otherValue")
            );
        }

        @Test
        void refusesPrintfInLogic7() {
            assertGeneratesMessage("The printf function requires language target 8 or higher.",
                    """
                            #set target = 7;
                            printf(format, name, value);
                            """);
        }
    }

}