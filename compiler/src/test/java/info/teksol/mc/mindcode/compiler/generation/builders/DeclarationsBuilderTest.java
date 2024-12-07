package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class DeclarationsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class ConstantDeclarations {
        @Test
        void compilesBinaryLiteralConstants() {
            assertCompilesTo("const c = 0b101; a = c;", createInstruction(SET, "a", "0b101"));
        }

        @Test
        void compilesBooleanLiteralConstants() {
            assertCompilesTo("const c = true; a = c;", createInstruction(SET, "a", "true"));
            assertCompilesTo("const c = false; a = c;", createInstruction(SET, "a", "false"));
        }

        @Test
        void compilesDecimalLiteralConstants() {
            assertCompilesTo("const c = 10; a = c;", createInstruction(SET, "a", "10"));
        }

        @Test
        void compilesFloatLiteralConstants() {
            assertCompilesTo("const c = 1e70; a = c;", createInstruction(SET, "a", "1E70"));
        }

        @Test
        void compilesNullLiteralConstants() {
            assertCompilesTo("const c = null; a = c;", createInstruction(SET, "a", "null"));
        }

        @Test
        void compilesBuiltInConstants() {
            assertCompilesTo("const c = @coal; a = c;", createInstruction(SET, "a", "@coal"));
        }

        @Test
        void compilesBlockConstants() {
            assertCompilesTo("const c = cell1; a = c;", createInstruction(SET, "a", "cell1"));
        }

        @Test
        void compilesExpressionConstantsFromLiterals() {
            assertCompilesTo("const c = 10 + 20; a = c;", createInstruction(SET, "a", "30"));
        }

        @Test
        void compilesExpressionConstantsFromConstants() {
            assertCompilesTo("const b = 10; const c = b + 20; a = c;", createInstruction(SET, "a", "30"));
        }

        @Test
        void compilesExpressionConstantsWithParentheses() {
            assertCompilesTo("""
                            const A = 10;
                            const B = 20;
                            const C = A * (B + 1);
                            print(C);
                            """,
                    createInstruction(PRINT, "210")
            );
        }

        @Test
        void compilesExpressionConstantsWithTernaryOps() {
            assertCompilesTo("""
                            const SLANTED = false;
                            const HEIGHT    = 12;
                            const HALF      = HEIGHT / 2 + (SLANTED ? 1 : 0);
                            print(HALF);
                            """,
                    createInstruction(PRINT, "6")
            );
        }

        @Test
        void compilesConstantsUsedInFunctions() {
            assertCompilesTo("""
                            const A = 10;
                            
                            foo();
                            void foo()
                                print(A);
                            end;
                            """,
                    createInstruction(PRINT, "10"),
                    createInstruction(LABEL, var(1001))
            );
        }

        @Test
        void compilesFunctionWithParameterIdenticalToConstant() {
            assertCompilesTo("""
                            const a = 10;
                            
                            void foo(a)
                                print(a);
                            end;
                            foo(5);
                            """,
                    createInstruction(SET, "__fn0_a", "5"),
                    createInstruction(PRINT, "__fn0_a"),
                    createInstruction(LABEL, var(1000))
            );
        }

        @Test
        void refusesNonConstantExpressions() {
            assertGeneratesMessage(
                    "Value assigned to constant 'a' is not a constant expression.",
                    "const a = b;");
        }

        @Test
        void refusesVolatileExpressions() {
            assertGeneratesMessage(
                    "Value assigned to constant 'a' is not a constant expression.",
                    "const a = @time;");
        }

        @Test
        void refusesMlog7UnrepresentableValues() {
            assertGeneratesMessage(
                    "Value '1e70' does not have a valid mlog representation.",
                    "#set target = 7; const a = 1e70;");
        }

        @Test
        void refusesUnrepresentableValues() {
            assertGeneratesMessage(
                    "Value '1e400' does not have a valid mlog representation.",
                    "const a = 1e400;");
        }

        @Test
        void refusesMultipleConstDeclarations() {
            assertGeneratesMessage(
                    "Multiple declarations of 'a'.",
                    "const a = 10; const a = 10;");
        }

        @Test
        void refusesMultipleParamDeclarations() {
            assertGeneratesMessage(
                    "Multiple declarations of 'a'.",
                    "const a = 10; param a = 10;");
        }

        @Test
        void refusesDeclarationOverLocalVariable() {
            assertGeneratesMessage(
                    "Multiple declarations of 'a'.",
                    "a = 10; const a = 10;");
        }

        @Test
        void refusesDeclarationOverGlobalVariable() {
            assertGeneratesMessage(
                    "Multiple declarations of 'A'.",
                    "A = 10; const A = 10;");
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
            assertCompilesTo("""
                            allocate heap in bank1;
                            $A = $B;
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "bank1", "null"),
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
                            .add("No heap allocated for external variables.").repeat(2),
                    "$A = $B; $B = 10;");

            assertGeneratesMessages(
                    expectedMessages()
                            .add("No heap allocated for external variables."),
                    """
                            $A = 10;
                            allocate heap in cell1[0 ... 1];
                            $B = $A;
                            """);
        }

        @Test
        void refusesDeclarationInsideCodeBlock() {
            assertGeneratesMessage(
                    "Statement or declaration not allowed within a main code block or function.",
                    "begin allocate heap in cell1; end;");
        }
    }

    @Nested
    class ParameterDeclarations {
        @Test
        void compilesLiteralParameters() {
            assertCompilesTo("param a = 0b101;", createInstruction(SET, "a", "0b101"));
            assertCompilesTo("param a = true;", createInstruction(SET, "a", "true"));
            assertCompilesTo("param a = false;", createInstruction(SET, "a", "false"));
            assertCompilesTo("param a = 10;", createInstruction(SET, "a", "10"));
            assertCompilesTo("param a = null;", createInstruction(SET, "a", "null"));
        }

        @Test
        void compilesBuiltInParameters() {
            assertCompilesTo("param a = @coal;", createInstruction(SET, "a", "@coal"));
        }

        @Test
        void compilesBlockParameters() {
            assertCompilesTo("param a = cell1;", createInstruction(SET, "a", "cell1"));
        }

        @Test
        void refusesNonConstantExpressions() {
            assertGeneratesMessage(
                    "Value assigned to parameter 'b' is not a constant expression.",
                    "param a = 10; param b = a;");
        }

        @Test
        void refusesVolatileExpressions() {
            assertGeneratesMessage(
                    "Value assigned to parameter 'a' is not a constant expression.",
                    "param a = @unit;");
        }

        @Test
        void refusesMlog7UnrepresentableValues() {
            assertGeneratesMessage(
                    "Value '1e70' does not have a valid mlog representation.",
                    "#set target = 7; param a = 1e70;\n");
        }

        @Test
        void refusesUnrepresentableValues() {
            assertGeneratesMessage(
                    "Value '1e400' does not have a valid mlog representation.",
                    "param a = 1e400;");
        }

        @Test
        void refusesMultipleParamDeclarations() {
            assertGeneratesMessage("Multiple declarations of 'a'.",
                    "param a = 10; param a = 10;");
        }

        @Test
        void refusesMultipleConstDeclarations() {
            assertGeneratesMessage("Multiple declarations of 'a'.",
                    "param a = 10; const a = 10;");
        }

        @Test
        void refusesDeclarationOverLocalVariable() {
            assertGeneratesMessage(
                    "Multiple declarations of 'a'.",
                    "a = 10; param a = 10;");
        }

        @Test
        void refusesDeclarationOverGlobalVariable() {
            assertGeneratesMessage(
                    "Multiple declarations of 'A'.",
                    "A = 10; param A = 10;");
        }

        @Test
        void refusesBlockNameAsParameterName() {
            assertGeneratesMessage(
                    "Name 'switch1' is reserved for linked blocks.",
                    "param switch1 = 10;");
        }

        @Test
        void refusesFormattablesAsParameters() {
            assertGeneratesMessage(
                    "Value assigned to parameter 'a' is not a constant expression.",
                    """
                            param a = $"Hello";
                            """);
        }
    }

    @Nested
    class VariableDeclarations {
        @Test
        void compilesVariableDeclarationWithLiteralInitializations() {
            assertCompilesTo("var a = 0b101;", createInstruction(SET, "a", "0b101"));
            assertCompilesTo("var a = true;", createInstruction(SET, "a", "true"));
            assertCompilesTo("var a = false;", createInstruction(SET, "a", "false"));
            assertCompilesTo("var a = 10;", createInstruction(SET, "a", "10"));
            assertCompilesTo("var a = null;", createInstruction(SET, "a", "null"));
        }

        @Test
        void compilesVariableDeclarationWithBuiltInInitializations() {
            assertCompilesTo("var a = @coal;", createInstruction(SET, "a", "@coal"));
        }

        @Test
        void compilesExternalVariableDeclarations() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external a;
                            external b = a;
                            print(b);
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "cell1", "null"),
                    createInstruction(READ, var(0), "cell1", "0"),
                    createInstruction(WRITE, var(0), "cell1", "1"),
                    createInstruction(READ, var(1), "cell1", "1"),
                    createInstruction(PRINT, var(1))
            );
        }

        @Test
        void compilesLinkedVariables() {
            assertCompilesTo("""
                            linked switch1, message1;
                            printflush(message1);
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "switch1", "null"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1001), "equal", "message1", "null"),
                    createInstruction(PRINTFLUSH, "message1")
            );
        }

        @Test
        void compilesLocalVariableDeclarations() {
            assertCompilesTo("""
                            inline void foo()
                                var a = 10;
                                print(a);
                            end;
                            
                            foo();
                            foo();
                            """,
                    createInstruction(SET, ":fn0:a", "10"),
                    createInstruction(PRINT, ":fn0:a"),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, ":fn1:a", "10"),
                    createInstruction(PRINT, ":fn1:a"),
                    createInstruction(LABEL, var(1001))
            );
        }

        @Test
        void compilesLocalVariableDeclarationsOverGlobalVariable() {
            assertCompilesTo("""
                            var a = 1;
                            
                            inline void foo()
                                var a = 10;
                                print(a);
                            end;
                            
                            print(a);
                            foo();
                            """,
                    createInstruction(SET, ".a", "1"),
                    createInstruction(PRINT, ".a"),
                    createInstruction(SET, ":fn0:a", "10"),
                    createInstruction(PRINT, ":fn0:a"),
                    createInstruction(LABEL, var(1000))
            );
        }

        @Test
        void compilesMainVariableDeclarationsOverGlobalVariable() {
            assertCompilesTo("""
                            var a = 1;
                            
                            inline void foo()
                                print(a);
                            end;

                            begin
                                var a = 10;
                                print(a);
                                foo();
                            end;
                            """,
                    createInstruction(SET, ".a", "1"),
                    createInstruction(SET, ":a", "10"),
                    createInstruction(PRINT, ":a"),
                    createInstruction(PRINT, ".a"),
                    createInstruction(LABEL, var(1000))
            );
        }

        @Test
        void compilesGlobalDeclarationOverLocalVariable() {
            assertCompiles("a = 10; var a = 10;");
        }

        @Test
        void compilesUninitializedVariable() {
            assertCompiles("noinit var a;");
        }

        @Test
        void refusesMultipleVariableDeclarations() {
            assertGeneratesMessage(
                    "Multiple declarations of 'a'.",
                    "var a = 10; var a = 10;");
        }

        @Test
        void refusesMultipleParamDeclarations() {
            assertGeneratesMessages(expectedMessages()
                            .add("Multiple declarations of 'a'.")
                            .add("Assignment to constant or parameter 'a' not allowed."),
                    "param a = 10; var a = 10;");
        }

        @Test
        void refusesMultipleConstDeclarations() {
            assertGeneratesMessages(expectedMessages()
                            .add("Multiple declarations of 'a'.")
                            .add("Assignment to constant or parameter 'a' not allowed."),
                    "const a = 10; var a = 10;");
        }

        @Test
        void refusesDeclarationOverLinkedVariable() {
            assertGeneratesMessage(
                    "Multiple declarations of 'message1'.",
                    """
                            printflush(message1);
                            linked message1;
                            """);
        }

        @Test
        void refusesDeclarationOverGlobalVariable() {
            assertGeneratesMessage(
                    "Multiple declarations of 'A'.",
                    "A = 10; var A = 10;");
        }

        @Test
        void refusesFormattablesAsParameters() {
            assertGeneratesMessage(
                    "A formattable string literal can only be used as a first argument to the print or remark function.",
                    """
                            var a = $"Hello";
                            """);
        }

        @Test
        void reportsNonstandardBlockName() {
            assertGeneratesMessage(
                    "Linked variable name 'foobar' doesn't correspond to any known linked block name.",
                    "linked foobar;");
        }

        @Test
        void reportsWrongUninitializedModifier() {
            assertGeneratesMessage(
                    "Variable declared as 'noinit' cannot be initialized.",
                    "noinit var a = 10;");
        }
    }
}
