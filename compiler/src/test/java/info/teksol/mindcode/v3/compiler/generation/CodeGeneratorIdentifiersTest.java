package info.teksol.mindcode.v3.compiler.generation;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class CodeGeneratorIdentifiersTest extends AbstractCodeGeneratorTest {

    @Nested
    class ArrayAccess {
        @Test
        void compilesSimpleArrayAccess() {
            assertCompiles("""
                            cell1[a] = cell2[b];
                            """,
                    createInstruction(READ, var(1), "cell2", "b"),
                    createInstruction(WRITE, var(1), "cell1", "a")
            );
        }

        @Test
        void compilesComplexArrayAccess() {
            assertCompiles("""
                            a = cell1;
                            a[a[x]] = a[x];
                            """,
                    createInstruction(SET, "a", "cell1"),
                    createInstruction(READ, var(0), "a", "x"),
                    createInstruction(READ, var(2), "a", "x"),
                    createInstruction(WRITE, var(2), "a", var(0))
            );
        }

        @Test
        void compilesMultipleArrayAccesses() {
            assertCompiles("""
                            cell1[a] += cell1[b] += cell1[c];
                            """,
                    createInstruction(READ, var(2), "cell1", "c"),
                    createInstruction(READ, var(1), "cell1", "b"),
                    createInstruction(OP, "add", var(3), var(1), var(2)),
                    createInstruction(WRITE, var(3), "cell1", "b"),
                    createInstruction(READ, var(0), "cell1", "a"),
                    createInstruction(OP, "add", var(4), var(0), var(3)),
                    createInstruction(WRITE, var(4), "cell1", "a")
            );
        }

        @Test
        void compilesChainedAssignmentsWithVolatileVariables() {
            assertCompiles("""
                            a = cell1[b] = @time;
                            """,
                    createInstruction(SET, var(1), "@time"),
                    createInstruction(WRITE, var(1), "cell1", "b"),
                    createInstruction(SET, "a", var(1))
            );
        }

        @Test
        void compilesSuspiciousArrayAccess() {
            assertCompiles("""
                            a = b + c;
                            a[0] = 1;
                            """,
                    createInstruction(OP, "add", var(0), "b", "c"),
                    createInstruction(SET, "a", var(0)),
                    createInstruction(WRITE, "1", "a", "0")
            );
        }

        @Test
        void refusesInvalidMemoryDeclarationParameter() {
            assertGeneratesMessages(
                    expectedMessages().add("Cannot use value assigned to parameter 'memory' to access external memory.").atLeast(1),
                    """
                            param memory = @coal;
                            memory[0] = 1;
                            """);
        }
    }
}
