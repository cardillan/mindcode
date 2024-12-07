package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.SET;

@NullMarked
class FunctionDeclarationsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class RelaxedDeclarations {

        @Test
        void compilesCodeWithoutUnusedFunctions() {
            assertGeneratesMessage(
                    "Parameter 'Z' of function 'foo' uses name reserved for global variables.",
                    "void foo(Z) end;");
        }
    }

    @Nested
    class StrictDeclarations {

        @Test
        void compilesCodeWithoutUnusedFunctions() {
            assertCompiles("""
                    #set syntax = strict;
                    void foo(Z) end;""");
        }
    }

    @Nested
    class UnusedFunctions {

        @Test
        void compilesCodeWithoutUnusedFunctions() {
            assertCompilesTo("""
                            void foo(x)
                                a = x;
                            end;
                            x = 10;
                            """,
                    createInstruction(SET, "x", "10")
            );
        }

        @Test
        void reportsErrorsInsideUnusedFunctions() {
            assertGeneratesMessages(
                    expectedMessages().add("Assignment to constant or parameter 'a' not allowed."),
                    """
                            const a = 10;
                            void foo(x)
                                a = x;
                            end;
                            """);
        }

        @Test
        void reportsErrorsInsideUnusedInlineFunctions() {
            assertGeneratesMessages(
                    expectedMessages().add("Assignment to constant or parameter 'a' not allowed."),
                    """
                            param a = 10;
                            inline void foo(x)
                                a = x;
                            end;
                            """);
        }
    }
}
