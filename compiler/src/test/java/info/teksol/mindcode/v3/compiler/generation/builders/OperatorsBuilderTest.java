package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.v3.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.OP;
import static info.teksol.mindcode.logic.Opcode.SET;

class OperatorsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class BinaryOperators {
        @Test
        void compilesPower() {
            assertCompiles("""
                            a ** b;
                            """,
                    createInstruction(OP, "pow", var(0), "a", "b")
            );
        }

        @Test
        void compilesMultiplications() {
            assertCompiles("""
                            a * b;
                            c / d;
                            e \\ f;
                            g % h;
                            """,
                    createInstruction(OP, "mul", var(0), "a", "b"),
                    createInstruction(OP, "div", var(1), "c", "d"),
                    createInstruction(OP, "idiv", var(2), "e", "f"),
                    createInstruction(OP, "mod", var(3), "g", "h")
            );
        }

        @Test
        void compilesAdditions() {
            assertCompiles("""
                            a + b;
                            c - d;
                            """,
                    createInstruction(OP, "add", var(0), "a", "b"),
                    createInstruction(OP, "sub", var(1), "c", "d")
            );
        }

        @Test
        void compilesShiftOperations() {
            assertCompiles("""
                            a << b;
                            c >> d;
                            """,
                    createInstruction(OP, "shl", var(0), "a", "b"),
                    createInstruction(OP, "shr", var(1), "c", "d")
            );
        }

        @Test
        void compilesAndOperations() {
            assertCompiles("""
                            a & b;
                            c && d;
                            e and f;
                            """,
                    createInstruction(OP, "and", var(0), "a", "b"),
                    createInstruction(OP, "land", var(1), "c", "d"),
                    createInstruction(OP, "land", var(2), "e", "f")
            );
        }

        @Test
        void compilesOrXorOperations() {
            assertCompiles("""
                            a | b;
                            c || d;
                            e or f;
                            g ^ h;
                            """,
                    createInstruction(OP, "or", var(0), "a", "b"),
                    createInstruction(OP, "or", var(2), "c", "d"),
                    createInstruction(OP, "notEqual", var(1), var(2), "false"),
                    createInstruction(OP, "or", var(3), "e", "f"),
                    createInstruction(OP, "xor", var(4), "g", "h")
            );
        }

        @Test
        void compilesInequalities() {
            assertCompiles("""
                            a < b;
                            c <= d;
                            e > f;
                            g >= h;
                            """,
                    createInstruction(OP, "lessThan", var(0), "a", "b"),
                    createInstruction(OP, "lessThanEq", var(1), "c", "d"),
                    createInstruction(OP, "greaterThan", var(2), "e", "f"),
                    createInstruction(OP, "greaterThanEq", var(3), "g", "h")
            );
        }

        @Test
        void compilesEqualities() {
            assertCompiles("""
                            a == b;
                            c != d;
                            e === f;
                            g !== h;
                            """,
                    createInstruction(OP, "equal", var(0), "a", "b"),
                    createInstruction(OP, "notEqual", var(1), "c", "d"),
                    createInstruction(OP, "strictEqual", var(2), "e", "f"),
                    createInstruction(OP, "strictEqual", var(4), "g", "h"),
                    createInstruction(OP, "equal", var(3), var(4), "false")
            );
        }

    }

    @Nested
    class UnaryOperators {
        @Test
        void compilesUnaryMinus() {
            assertCompiles("a = -b;",
                    createInstruction(OP, "sub", var(0), "0", "b"),
                    createInstruction(SET, "a", var(0))
            );
        }

        @Test
        void compilesUnaryPlus() {
            assertCompiles("a = +b;",
                    createInstruction(SET, "a", "b")
            );
        }

        @Test
        void compilesBitwiseNot() {
            assertCompiles("a = ~b;",
                    createInstruction(OP, "not", var(0), "b"),
                    createInstruction(SET, "a", var(0))
            );
        }

        @Test
        void compilesBooleanNot1() {
            assertCompiles("a = not b;",
                    createInstruction(OP, "equal", var(0), "b", "false"),
                    createInstruction(SET, "a", var(0))
            );
        }

        @Test
        void compilesBooleanNot2() {
            assertCompiles("a = !b;",
                    createInstruction(OP, "equal", var(0), "b", "false"),
                    createInstruction(SET, "a", var(0))
            );
        }
    }
}
