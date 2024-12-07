package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class AssignmentsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class CompoundAssignments {
        @Test
        void compilesPowerAssignment() {
            assertCompilesTo("""
                            c = a **= b;
                            """,
                    createInstruction(OP, "pow", "a", "a", "b"),
                    createInstruction(SET, "c", "a")
            );
        }

        @Test
        void compilesMultiplications() {
            assertCompilesTo("""
                            a *= b;
                            c /= d;
                            e \\= f;
                            g %= h;
                            """,
                    createInstruction(OP, "mul", "a", "a", "b"),
                    createInstruction(OP, "div", "c", "c", "d"),
                    createInstruction(OP, "idiv", "e", "e", "f"),
                    createInstruction(OP, "mod", "g", "g", "h")
            );
        }

        @Test
        void compilesAdditions() {
            assertCompilesTo("""
                            a += b;
                            c -= d;
                            """,
                    createInstruction(OP, "add", "a", "a", "b"),
                    createInstruction(OP, "sub", "c", "c", "d")
            );
        }

        @Test
        void compilesShiftOperations() {
            assertCompilesTo("""
                            a <<= b;
                            c >>= d;
                            """,
                    createInstruction(OP, "shl", "a", "a", "b"),
                    createInstruction(OP, "shr", "c", "c", "d")
            );
        }

        @Test
        void compilesAndOperations() {
            assertCompilesTo("""
                            a &= b;
                            c &&= d;
                            """,
                    createInstruction(OP, "and", "a", "a", "b"),
                    createInstruction(OP, "land", "c", "c", "d")
            );
        }

        @Test
        void compilesOrXorOperations() {
            assertCompilesTo("""
                            a |= b;
                            c ||= d;
                            g ^= h;
                            """,
                    createInstruction(OP, "or", "a", "a", "b"),
                    createInstruction(OP, "or", var(1), "c", "d"),
                    createInstruction(OP, "notEqual", "c", var(1), "false"),
                    createInstruction(OP, "xor", "g", "g", "h")
            );
        }

        @Test
        void compilesExternapVariablesWithCompoundAssignments() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            $A += $B;
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "bank1", "null"),
                    createInstruction(READ, var(1), "bank1", "1"),
                    createInstruction(READ, var(0), "bank1", "0"),
                    createInstruction(OP, "add", var(2), var(0), var(1)),
                    createInstruction(WRITE, var(2), "bank1", "0")
            );
        }
    }


    @Nested
    class IncrementDecrementOperators {

        @Test
        void compilesBasicPrefixOperators() {
            assertCompilesTo("""
                            a = ++b;
                            c = --d;
                            """,
                    createInstruction(OP, "add", "b", "b", "1"),
                    createInstruction(SET, "a", "b"),
                    createInstruction(OP, "sub", "d", "d", "1"),
                    createInstruction(SET, "c", "d")
            );
        }

        @Test
        void compilesBasicPostfixOperators() {
            assertCompilesTo("""
                            a = b++;
                            c = d--;
                            """,
                    createInstruction(SET, var(0), "b"),
                    createInstruction(OP, "add", "b", "b", "1"),
                    createInstruction(SET, "a", var(0)),
                    createInstruction(SET, var(1), "d"),
                    createInstruction(OP, "sub", "d", "d", "1"),
                    createInstruction(SET, "c", var(1))
            );
        }

        @Test
        void compilesComplexPrefixPostfixExpressions() {
            assertCompilesTo("""
                            a = b++ * --c;
                            """,
                    createInstruction(SET, var(0), "b"),
                    createInstruction(OP, "add", "b", "b", "1"),
                    createInstruction(OP, "sub", "c", "c", "1"),
                    createInstruction(OP, "mul", var(1), var(0), "c"),
                    createInstruction(SET, "a", var(1))
            );
        }

        @Test
        void compilesExternalVariablePrefixPostfixExpressions() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            $A = $B++ * --$C;
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "bank1", "null"),
                    createInstruction(READ, var(1), "bank1", "1"),
                    createInstruction(SET, var(2), var(1)),
                    createInstruction(OP, "add", var(1), var(1), "1"),
                    createInstruction(WRITE, var(1), "bank1", "1"),
                    createInstruction(READ, var(3), "bank1", "2"),
                    createInstruction(OP, "sub", var(4), var(3), "1"),
                    createInstruction(WRITE, var(4), "bank1", "2"),
                    createInstruction(OP, "mul", var(5), var(2), var(4)),
                    createInstruction(WRITE, var(5), "bank1", "0")
            );
        }

        @Test
        void compilesArrayAccessPrefixPostfixExpressions() {
            assertCompilesTo("""
                            cell1[a] = cell2[b]++ * --cell3[c];
                            """,
                    createInstruction(SET, var(0), "a"),
                    createInstruction(SET, var(2), "b"),
                    createInstruction(READ, var(3), "cell2", var(2)),
                    createInstruction(SET, var(4), var(3)),
                    createInstruction(OP, "add", var(3), var(3), "1"),
                    createInstruction(WRITE, var(3), "cell2", var(2)),
                    createInstruction(SET, var(5), "c"),
                    createInstruction(READ, var(6), "cell3", var(5)),
                    createInstruction(OP, "sub", var(7), var(6), "1"),
                    createInstruction(WRITE, var(7), "cell3", var(5)),
                    createInstruction(OP, "mul", var(8), var(4), var(7)),
                    createInstruction(WRITE, var(8), "cell1", var(0))
            );
        }

        @Test
        void compilesArrayAccessPrefixPostfixIndexExpressions() {
            assertCompilesTo("""
                            cell1[a++]++;
                            cell2[--a]--;
                            """,
                    createInstruction(SET, var(0), "a"),
                    createInstruction(OP, "add", "a", "a", "1"),
                    createInstruction(SET, var(1), var(0)),
                    createInstruction(READ, var(2), "cell1", var(1)),
                    createInstruction(SET, var(3), var(2)),
                    createInstruction(OP, "add", var(2), var(2), "1"),
                    createInstruction(WRITE, var(2), "cell1", var(1)),
                    createInstruction(OP, "sub", "a", "a", "1"),
                    createInstruction(SET, var(4), "a"),
                    createInstruction(READ, var(5), "cell2", var(4)),
                    createInstruction(SET, var(6), var(5)),
                    createInstruction(OP, "sub", var(5), var(5), "1"),
                    createInstruction(WRITE, var(5), "cell2", var(4))
            );
        }

        @Test
        void refusesAssignmentToLiteral() {
            assertGeneratesMessages(expectedMessages()
                            .add("Variable expected."),
                    "10 = a;");
        }
    }

    @Nested
    class InvalidAssignments {
        @Test
        void refusesAssignmentToLiteral() {
            assertGeneratesMessages(expectedMessages()
                            .add("Variable expected."),
                    "10 = a;");
        }

        @Test
        void refusesAssignmentToConstants() {
            assertGeneratesMessages(expectedMessages()
                            .add("Assignment to constant or parameter 'a' not allowed."),
                    """
                            const a = 10;
                            a = 20;
                            """);
        }

        @Test
        void refusesAssignmentToParameters() {
            assertGeneratesMessages(expectedMessages()
                            .add("Assignment to constant or parameter 'a' not allowed."),
                    """
                            param a = 10;
                            a = 20;
                            """);
        }

        @Test
        void refusesAssignmentToExpression() {
            assertGeneratesMessages(expectedMessages()
                            .add("Cannot assign a value to this expression."),
                    """
                            a + b = c;
                            """);
        }

        @Test
        void refusesAssigningFormattables() {
            assertGeneratesMessage("A formattable string literal can only be used as a first argument to the print or remark function.",
                    """
                            a = $"Hello";
                            """);
        }

        @Test
        void generatesNoValueWarnings() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add("Expression doesn't have any value. Using value-less expressions in assignments is deprecated.")
                            .add("Expression doesn't have any value. Using value-less expressions in function calls is deprecated.")
                            .add("Expression doesn't have any value. Using value-less expressions in return statements is deprecated."),
                    """
                            void foo()
                                null;
                            end;
    
                            def bar()
                                x = foo();
                                print(foo());
                                return foo();
                            end;
    
                            bar();
                            """
            );
        }
    }

    @Nested
    class SimpleAssignments {
        @Test
        void compilesLiteralAssignments() {
            assertCompilesTo("a = 0b101;", createInstruction(SET, "a", "0b101"));
            assertCompilesTo("a = true;", createInstruction(SET, "a", "true"));
            assertCompilesTo("a = false;", createInstruction(SET, "a", "false"));
            assertCompilesTo("a = 10;", createInstruction(SET, "a", "10"));
            assertCompilesTo("a = 1.05;", createInstruction(SET, "a", "1.05"));
            assertCompilesTo("a = 1.50e10;", createInstruction(SET, "a", "15000000000"));
            assertCompilesTo("a = 1.50e80;", createInstruction(SET, "a", "15E79"));
            assertCompilesTo("a = 0x10;", createInstruction(SET, "a", "0x10"));
            assertCompilesTo("a = null;", createInstruction(SET, "a", "null"));
        }

        @Test
        void compilesNegativeLiteralAssignment() {
            assertCompilesTo("""
                            a = -10;
                            """,
                    createInstruction(SET, "a", "-10"));
        }

        @Test
        void compilesNegativeBinaryLiteralAssignment() {
            assertCompilesTo("""
                            a = -0b1111;
                            """,
                    createInstruction(SET, "a", "-15"));
        }

        @Test
        void compilesNegativeHexadecimalLiteralAssignment() {
            assertCompilesTo("""
                            a = -0xFF;
                            """,
                    createInstruction(SET, "a", "-255"));
        }

        @Test
        void compilesStringLiteralAssignment() {
            assertCompilesTo("""
                            a = "Hello";
                            """,
                    createInstruction(SET, "a", AbstractCodeGeneratorTest.q("Hello")));
        }

        @Test
        void compilesChainedConstantAssignments() {
            assertCompilesTo("""
                            a = b = 10;
                            """,
                    createInstruction(SET, "b", "10"),
                    createInstruction(SET, "a", "b")
            );
        }

        @Test
        void compilesChainedExpressionAssignments() {
            assertCompilesTo("""
                            a = b = c + d;
                            """,
                    createInstruction(OP, "add", var(0), "c", "d"),
                    createInstruction(SET, "b", var(0)),
                    createInstruction(SET, "a", "b")
            );
        }

        @Test
        void refusesVoidAssignments() {
            assertGeneratesMessages(expectedMessages()
                            .addRegex(1, 5, "Parse error: extraneous input 'const' expecting.*"),
                    "a = const b = 0;");
        }

        @Test
        void refusesInvalidAssignments() {
            assertGeneratesMessages(expectedMessages()
                            .add("Cannot assign a value to this expression."),
                    "a + b = 10;");
        }
    }
}
