package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.v3.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class DeclarationsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class ConstantDeclarations {
        @Test
        void compilesLiteralConstants() {
            assertCompiles("const c = 0b101; a = c;", createInstruction(SET, "a", "0b101"));
            assertCompiles("const c = true; a = c;", createInstruction(SET, "a", "true"));
            assertCompiles("const c = false; a = c;", createInstruction(SET, "a", "false"));
            assertCompiles("const c = 10; a = c;", createInstruction(SET, "a", "10"));
            assertCompiles("const c = null; a = c;", createInstruction(SET, "a", "null"));
        }

        @Test
        void compilesBuiltInConstants() {
            assertCompiles("const c = @coal; a = c;", createInstruction(SET, "a", "@coal"));
        }

        @Test
        void compilesBlockConstants() {
            assertCompiles("const c = cell1; a = c;", createInstruction(SET, "a", "cell1"));
        }

        @Test
        void compilesExpressionConstantsFromLiterals() {
            assertCompiles("const c = 10 + 20; a = c;", createInstruction(SET, "a", "30"));
        }

        @Test
        void compilesExpressionConstantsFromConstants() {
            assertCompiles("const b = 10; const c = b + 20; a = c;", createInstruction(SET, "a", "30"));
        }

        @Test
        void refusesNonConstantExpressions() {
            assertGeneratesMessages(
                    expectedMessages().add("Value assigned to constant 'a' is not a constant expression."),
                    "const a = b; const b = 20;");
        }

        @Test
        void refusesVolatileExpressions() {
            assertGeneratesMessages(
                    expectedMessages().add("Value assigned to constant 'a' is not a constant expression."),
                    "const a = @time;");
        }

        @Test
        void refusesMlog7UnrepresentableValues() {
            assertGeneratesMessages(
                    expectedMessages().add("Numeric literal '1e70' does not have a valid mlog representation."),
                    """
                            #set target = ML7A;
                            const a = 1e70;
                            """);
        }

        @Test
        void refusesUnrepresentableValues() {
            assertGeneratesMessages(
                    expectedMessages().add("Numeric literal '1e400' does not have a valid mlog representation."),
                    "const a = 1e400;");
        }
    }

    @Nested
    class HeapAllocationDeclarations {

        @Test
        void acceptsMemoryDeclarationConstant() {
            assertCompiles("""
                    const memory = cell1;
                    allocate heap in memory[0 ... 64];
                    """);
        }

        @Test
        void acceptsMemoryDeclarationParameter() {
            assertCompiles("""
                    param memory = bank1;
                    allocate heap in memory[0 ... 512];
                    """);
        }

        @Test
        void compilesExternalVariables() {
            assertCompiles("""
                            allocate heap in bank1;
                            $A = $B;
                            """,
                    createInstruction(READ, var(1), "bank1", "1"),
                    createInstruction(WRITE, var(1), "bank1", "0")
            );
        }

        @Test
        void refusesInvalidMemoryDeclarationConstant() {
            assertGeneratesMessages(
                    expectedMessages().add("Cannot use 'memory' as a memory for heap or stack."),
                    """
                            const memory = @coal;
                            allocate heap in memory;
                            """);
        }

        @Test
        void refusesInvalidMemoryDeclarationParameter() {
            assertGeneratesMessages(
                    expectedMessages().add("Cannot use value assigned to parameter 'memory' as a memory for heap or stack."),
                    """
                            param memory = @coal;
                            allocate heap in memory;
                            """);
        }

        @Test
        void refusesMultipleDeclarations() {
            assertGeneratesMessages(
                    expectedMessages().add("Multiple heap allocation declarations."),
                    """
                            allocate heap in memory;
                            allocate heap in bank1;
                            """);
        }

        @Test
        void refusesNonConstantRanges() {
            assertGeneratesMessages(
                    expectedMessages().add("Heap/stack declaration must specify constant range."),
                    "allocate heap in bank1[a .. 10];");
            assertGeneratesMessages(
                    expectedMessages().add("Heap/stack declaration must specify constant range."),
                    "allocate heap in bank1[0 .. b];");
        }

        @Test
        void refusesNonIntegerRanges() {
            assertGeneratesMessages(
                    expectedMessages().add("Heap/stack declaration must specify integer range."),
                    "allocate heap in bank1[10.5 .. 20];");
            assertGeneratesMessages(
                    expectedMessages().add("Heap/stack declaration must specify integer range."),
                    "allocate heap in bank1[0 .. 15.8];");
        }

        @Test
        void refusesOutOfRangeRanges() {
            assertCompiles("allocate heap in bank1[0 ... 512];");
            assertCompiles("allocate heap in bank1[0 .. 511];");
            assertGeneratesMessages(
                    expectedMessages().add("Heap/stack memory index out of range (0 .. 512)."),
                    "allocate heap in bank1[0 ... 513];");
            assertGeneratesMessages(
                    expectedMessages().add("Heap/stack memory index out of range (0 .. 512)."),
                    "allocate heap in bank1[0 .. 512];");
            assertGeneratesMessages(
                    expectedMessages().add("Heap/stack memory index out of range (0 .. 512)."),
                    "allocate heap in bank1[-1 .. 0];");
            assertGeneratesMessages(
                    expectedMessages().add("Heap/stack memory index out of range (0 .. 512)."),
                    "allocate heap in bank1[512 ... 512];");
        }

        @Test
        void refusesEmptyRanges() {
            assertCompiles("allocate heap in bank1[0 .. 0];");
            assertCompiles("allocate heap in bank1[1 ... 2];");
            assertGeneratesMessages(
                    expectedMessages().add("Empty or invalid heap/stack memory range."),
                    "allocate heap in bank1[0 ... 0];");
            assertGeneratesMessages(
                    expectedMessages().add("Empty or invalid heap/stack memory range."),
                    "allocate heap in bank1[1 .. 0];");
        }

        @Test
        void refusesTooManyVariables() {
            // Make sure each excess variable is reported only once
            assertGeneratesMessages(
                    expectedMessages()
                            .add("Not enough capacity in allocated heap for '$B'.")
                            .add("Not enough capacity in allocated heap for '$C'."),
                    """
                            allocate heap in cell1[0 ... 1];
                            $A = $B;
                            $B = $C;
                            """);
        }

        @Test
        void refusesMissingHeapDeclaration() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add("The heap must be allocated before using it.").repeat(2),
                    "$A = $B; $B = 10;");

            assertGeneratesMessages(
                    expectedMessages()
                            .add("The heap must be allocated before using it."),
                    """
                            $A = 10;
                            allocate heap in cell1[0 ... 1];
                            $B = $A;
                            """);
        }

        @Test
        void refusesDeclarationInsideCodeBlock() {
            assertGeneratesMessages(
                    expectedMessages().add("Heap/stack allocation must be declared at the topmost level."),
                    "begin allocate heap in cell1; end;");
        }
    }

    @Nested
    class ParameterDeclarations {
        @Test
        void compilesLiteralConstants() {
            assertCompiles("param a = 0b101;", createInstruction(SET, "a", "0b101"));
            assertCompiles("param a = true;", createInstruction(SET, "a", "true"));
            assertCompiles("param a = false;", createInstruction(SET, "a", "false"));
            assertCompiles("param a = 10;", createInstruction(SET, "a", "10"));
            assertCompiles("param a = null;", createInstruction(SET, "a", "null"));
        }

        @Test
        void compilesBuiltInConstants() {
            assertCompiles("param a = @coal;", createInstruction(SET, "a", "@coal"));
        }

        @Test
        void compilesBlockConstants() {
            assertCompiles("param a = cell1;", createInstruction(SET, "a", "cell1"));
        }

        @Test
        void refusesNonConstantExpressions() {
            assertGeneratesMessages(
                    expectedMessages().add("Value assigned to parameter 'b' is not a constant expression."),
                    "param a = 10; param b = a;");
        }

        @Test
        void refusesVolatileExpressions() {
            assertGeneratesMessages(
                    expectedMessages().add("Value assigned to parameter 'a' is not a constant expression."),
                    "param a = @unit;");
        }

        @Test
        void refusesMlog7UnrepresentableValues() {
            assertGeneratesMessages(
                    expectedMessages().add("Numeric literal '1e70' does not have a valid mlog representation."),
                    """
                            #set target = ML7A;
                            param a = 1e70;
                            """);
        }

        @Test
        void refusesUnrepresentableValues() {
            assertGeneratesMessages(
                    expectedMessages().add("Numeric literal '1e400' does not have a valid mlog representation."),
                    "param a = 1e400;");
        }
    }
}
