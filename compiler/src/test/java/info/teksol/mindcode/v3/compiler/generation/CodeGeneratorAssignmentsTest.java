package info.teksol.mindcode.v3.compiler.generation;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class CodeGeneratorAssignmentsTest extends AbstractCodeGeneratorTest {

    @Nested
    class CompoundAssignments {
        @Test
        void compilesPowerAssignment() {
            assertCompiles("""
                            c = a **= b;
                            """,
                    createInstruction(OP, "pow", "a", "a", "b"),
                    createInstruction(SET, "c", "a")
            );
        }

        @Test
        void compilesMultiplications() {
            assertCompiles("""
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
            assertCompiles("""
                            a += b;
                            c -= d;
                            """,
                    createInstruction(OP, "add", "a", "a", "b"),
                    createInstruction(OP, "sub", "c", "c", "d")
            );
        }

        @Test
        void compilesShiftOperations() {
            assertCompiles("""
                            a <<= b;
                            c >>= d;
                            """,
                    createInstruction(OP, "shl", "a", "a", "b"),
                    createInstruction(OP, "shr", "c", "c", "d")
            );
        }

        @Test
        void compilesAndOperations() {
            assertCompiles("""
                            a &= b;
                            c &&= d;
                            """,
                    createInstruction(OP, "and", "a", "a", "b"),
                    createInstruction(OP, "land", "c", "c", "d")
            );
        }

        @Test
        void compilesOrXorOperations() {
            assertCompiles("""
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
            assertCompiles("""
                            allocate heap in bank1;
                            $A += $B;
                            """,
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
            assertCompiles("""
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
            assertCompiles("""
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
            assertCompiles("""
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
            assertCompiles("""
                            allocate heap in bank1;
                            $A = $B++ * --$C;
                            """,
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
            assertCompiles("""
                            cell1[a] = cell2[b]++ * --cell3[c];
                            """,
                    createInstruction(READ, var(1), "cell2", "b"),
                    createInstruction(SET, var(2), var(1)),
                    createInstruction(OP, "add", var(1), var(1), "1"),
                    createInstruction(WRITE, var(1), "cell2", "b"),
                    createInstruction(READ, var(3), "cell3", "c"),
                    createInstruction(OP, "sub", var(4), var(3), "1"),
                    createInstruction(WRITE, var(4), "cell3", "c"),
                    createInstruction(OP, "mul", var(5), var(2), var(4)),
                    createInstruction(WRITE, var(5), "cell1", "a")
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
    }

    @Nested
    class SimpleAssignments {
        @Test
        void compilesLiteralAssignments() {
            assertCompiles("a = 0b101;", createInstruction(SET, "a", "0b101"));
            assertCompiles("a = true;", createInstruction(SET, "a", "true"));
            assertCompiles("a = false;", createInstruction(SET, "a", "false"));
            assertCompiles("a = 10;", createInstruction(SET, "a", "10"));
            assertCompiles("a = 1.05;", createInstruction(SET, "a", "1.05"));
            assertCompiles("a = 1.50e10;", createInstruction(SET, "a", "15000000000"));
            assertCompiles("a = 1.50e80;", createInstruction(SET, "a", "15E79"));
            assertCompiles("a = 0x10;", createInstruction(SET, "a", "0x10"));
            assertCompiles("a = null;", createInstruction(SET, "a", "null"));
        }

        @Test
        void compilesNegativeLiteralAssignment() {
            assertCompiles("""
                            a = -10;
                            """,
                    createInstruction(SET, "a", "-10"));
        }

        @Test
        void compilesNegativeBinaryLiteralAssignment() {
            assertCompiles("""
                            a = -0b1111;
                            """,
                    createInstruction(SET, "a", "-15"));
        }

        @Test
        void compilesNegativeHexadecimalLiteralAssignment() {
            assertCompiles("""
                            a = -0xFF;
                            """,
                    createInstruction(SET, "a", "-255"));
        }

        @Test
        void compilesStringLiteralAssignment() {
            assertCompiles("""
                            a = "Hello";
                            """,
                    createInstruction(SET, "a", q("Hello")));
        }

        @Test
        void compilesChainedConstantAssignments() {
            assertCompiles("""
                            a = b = 10;
                            """,
                    createInstruction(SET, "b", "10"),
                    createInstruction(SET, "a", "b")
            );
        }

        @Test
        void compilesChainedExpressionAssignments() {
            assertCompiles("""
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
