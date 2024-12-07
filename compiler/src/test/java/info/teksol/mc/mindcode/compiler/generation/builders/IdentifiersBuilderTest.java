package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class IdentifiersBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class ArrayAccess {
        @Test
        void compilesSimpleArrayAccess() {
            assertCompilesTo("""
                            cell1[a] = cell2[b];
                            """,
                    createInstruction(SET, var(0), "a"),
                    createInstruction(SET, var(2), "b"),
                    createInstruction(READ, var(3), "cell2", var(2)),
                    createInstruction(WRITE, var(3), "cell1", var(0))
            );
        }

        @Test
        void compilesComplexArrayAccess() {
            assertCompilesTo("""
                            a = cell1;
                            a[a[x]] = a[x];
                            """,
                    createInstruction(SET, "a", "cell1"),
                    createInstruction(SET, var(0), "x"),
                    createInstruction(READ, var(1), "a", var(0)),
                    createInstruction(SET, var(2), var(1)),
                    createInstruction(SET, var(4), "x"),
                    createInstruction(READ, var(5), "a", var(4)),
                    createInstruction(WRITE, var(5), "a", var(2))
            );
        }

        @Test
        void compilesMultipleArrayAccesses() {
            assertCompilesTo("""
                            cell1[a] += cell1[b] += cell1[c];
                            """,
                    createInstruction(SET, var(0), "a"),
                    createInstruction(SET, var(2), "b"),
                    createInstruction(SET, var(4), "c"),
                    createInstruction(READ, var(5), "cell1", var(4)),
                    createInstruction(READ, var(3), "cell1", var(2)),
                    createInstruction(OP, "add", var(6), var(3), var(5)),
                    createInstruction(WRITE, var(6), "cell1", var(2)),
                    createInstruction(READ, var(1), "cell1", var(0)),
                    createInstruction(OP, "add", var(7), var(1), var(6)),
                    createInstruction(WRITE, var(7), "cell1", var(0))
            );
        }

        @Test
        void compilesChainedAssignmentsWithVolatileVariables() {
            assertCompilesTo("""
                            a = cell1[b] = @time;
                            """,
                    createInstruction(SET, var(0), "b"),
                    createInstruction(SET, var(2), "@time"),
                    createInstruction(WRITE, var(2), "cell1", var(0)),
                    createInstruction(SET, "a", var(2))
            );
        }

        @Test
        void compilesSuspiciousArrayAccess() {
            assertCompilesTo("""
                            a = b + c;
                            a[0] = 1;
                            """,
                    createInstruction(OP, "add", var(0), "b", "c"),
                    createInstruction(SET, "a", var(0)),
                    createInstruction(WRITE, "1", "a", "0")
            );
        }

        @Test
        void compilesMainMemoryVariable() {
            assertCompilesTo(
                    "memory = cell1; memory[0] = rand(9**9);",
                    createInstruction(SET, "memory", "cell1"),
                    createInstruction(OP, "rand", var(0), "387420489"),
                    createInstruction(WRITE, var(0), "memory", "0"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesMemoryAccessThroughParameter() {
            assertCompilesTo("""
                            param mem = bank1;
                            mem[0] = 5;
                            """,
                    createInstruction(SET, "mem", "bank1"),
                    createInstruction(WRITE, "5", "mem", "0"),
                    createInstruction(END)
            );
        }
    }

    @Nested
    class BuiltIns {
        @Test
        void compilesKnownBuiltIns() {
            assertCompiles("""
                            print(@coal);
                            print(@crux);
                            print(@time);
                            """
            );
        }


        @Test
        void warnsAtUnknownBuiltIns() {
            assertGeneratesMessages(expectedMessages()
                            .add("Built-in variable '@fluffy-bunny' not recognized."),
                    """
                            print(@fluffy-bunny);
                            """);
        }

    }

    @Nested
    class Identifiers {
        @Test
        void recognizesIconConstants() {
            assertCompilesTo("""
                            print(ITEM_COAL + "X");
                            """,
                    createInstruction(PRINT, q("\uF833X")),
                    createInstruction(END)
            );
        }
    }
}
