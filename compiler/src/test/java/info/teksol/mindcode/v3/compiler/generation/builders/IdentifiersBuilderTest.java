package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.v3.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class IdentifiersBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class ArrayAccess {
        @Test
        void compilesSimpleArrayAccess() {
            assertCompiles("""
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
            assertCompiles("""
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
            assertCompiles("""
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
            assertCompiles("""
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

        @Test
        void refusesAssigningFormattables() {
            assertGeneratesMessages(
                    expectedMessages().add("A formattable string literal can only be used as a first argument to the print or remark function."),
                    """
                            a = $"Hello";
                            """);
        }

        @Test
        void refusesFormattablesAsParameters() {
            assertGeneratesMessages(
                    expectedMessages().add("Value assigned to parameter 'a' is not a constant expression."),
                    """
                            param a = $"Hello";
                            """);
        }
    }
}
