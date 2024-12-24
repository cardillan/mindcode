package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.v3.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.SET;

class FunctionDeclarationsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class UnusedFunctions {

        @Test
        void compilesCodeWithoutUnusedFunctions() {
            assertCompiles("""
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
