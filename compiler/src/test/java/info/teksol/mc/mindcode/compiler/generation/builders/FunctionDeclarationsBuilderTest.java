package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.SET;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
class FunctionDeclarationsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class RelaxedDeclarations {

        @Test
        void reportsErrorInUnusedFunctions() {
            assertGeneratesMessage(
                    "Parameter 'Z' of function 'foo' uses name reserved for global variables.",
                    "void foo(Z) end;");
        }

        @Test
        void compilesCodeWithoutUnusedFunctions() {
            assertCompiles(
                    "void foo(z) end;");
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
    class CodeWeights {

        @Test
        void processesCodeWeightsInline() {
            compile(expectedMessages(),
                    """
                            foo();
                            
                            #setlocal weight = 2;
                            inline void foo()
                                print("foo");
                            end;
                            """,
                    compiler -> {
                        double weigth = compiler.getInstructions().stream()
                                .filter(i -> i.getOpcode() == Opcode.PRINT)
                                .mapToDouble(i -> i.getAstContext().totalWeight())
                                .findAny().orElse(0.0d);

                        assertEquals(2.0d, weigth);
                    });
        }

        @Test
        void processesCodeWeightsNoinline() {
            compile(expectedMessages(),
                    """
                            foo();
                            
                            #setlocal weight = 2;
                            noinline void foo()
                                print("foo");
                            end;
                            """,
                    compiler -> {
                        double weigth = compiler.getInstructions().stream()
                                .filter(i -> i.getOpcode() == Opcode.PRINT)
                                .mapToDouble(i -> i.getAstContext().totalWeight())
                                .findAny().orElse(0.0d);

                        assertEquals(2.0d, weigth);
                    });
        }

        @Test
        void processesCodeWeightsNoinlineMultiple() {
            compile(expectedMessages(),
                    """
                            foo();
                            foo();
                            
                            #setlocal weight = 2;
                            noinline void foo()
                                print("foo");
                            end;
                            """,
                    compiler -> {
                        double weigth = compiler.getInstructions().stream()
                                .filter(i -> i.getOpcode() == Opcode.PRINT)
                                .mapToDouble(i -> i.getAstContext().totalWeight())
                                .findAny().orElse(0.0d);

                        assertEquals(4.0d, weigth);
                    });
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
                    createInstruction(SET, ":x", "10")
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
