package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.OP;
import static info.teksol.mc.mindcode.logic.opcodes.Opcode.SET;

@NullMarked
class OperatorsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class BinaryOperators {
        @Test
        void compilesPower() {
            assertCompilesTo("""
                            a ** b;
                            """,
                    createInstruction(OP, "pow", var(0), "a", "b")
            );
        }

        @Test
        void compilesMultiplications() {
            assertCompilesTo("""
                            a * b;
                            c / d;
                            e \\ f;
                            g % h;
                            i %% j;
                            """,
                    createInstruction(OP, "mul", var(0), "a", "b"),
                    createInstruction(OP, "div", var(1), "c", "d"),
                    createInstruction(OP, "idiv", var(2), "e", "f"),
                    createInstruction(OP, "mod", var(3), "g", "h"),
                    createInstruction(OP, "emod", var(4), "i", "j")
            );
        }

        @Test
        void compilesAdditions() {
            assertCompilesTo("""
                            a + b;
                            c - d;
                            """,
                    createInstruction(OP, "add", var(0), "a", "b"),
                    createInstruction(OP, "sub", var(1), "c", "d")
            );
        }

        @Test
        void compilesShiftOperations() {
            assertCompilesTo("""
                            a << b;
                            c >> d;
                            e >>> f;
                            """,
                    createInstruction(OP, "shl", var(0), ":a", ":b"),
                    createInstruction(OP, "shr", var(1), ":c", ":d"),
                    createInstruction(OP, "ushr", var(2), ":e", ":f")
            );
        }

        @Test
        void compilesAndOperations() {
            assertCompilesTo("""
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
            assertCompilesTo("""
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
            assertCompilesTo("""
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
            assertCompilesTo("""
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

        @Test
        void emulatesEmodInTarget7() {
            assertCompilesTo("""
                            #set target = 7;
                            e %% f;
                            """,
                    createInstruction(OP, "mod", tmp(0), ":e", ":f"),
                    createInstruction(OP, "add", tmp(0), tmp(0), ":f"),
                    createInstruction(OP, "mod", tmp(0), tmp(0), ":f")
            );
        }

        @Test
        void reportsWrongTargetForUshr() {
            assertGeneratesMessage(
                    "The '>>>' operator requires language target 8 or higher.",
                    """
                            #set target = 7;
                            a >>> b;
                            """
            );
        }
    }

    @Nested
    class UnaryOperators {
        @Test
        void compilesUnaryMinus() {
            assertCompilesTo("a = -b;",
                    createInstruction(OP, "sub", var(0), "0", "b"),
                    createInstruction(SET, "a", var(0))
            );
        }

        @Test
        void compilesUnaryPlus() {
            assertCompilesTo("a = +b;",
                    createInstruction(SET, "a", "b")
            );
        }

        @Test
        void compilesBitwiseNot() {
            assertCompilesTo("a = ~b;",
                    createInstruction(OP, "not", var(0), "b"),
                    createInstruction(SET, "a", var(0))
            );
        }

        @Test
        void compilesBooleanNot() {
            assertCompilesTo("a = !b;",
                    createInstruction(OP, "equal", var(0), "b", "false"),
                    createInstruction(SET, "a", var(0))
            );
        }

        @Test
        void compilesLogicalNot() {
            assertCompilesTo("a = not b;",
                    createInstruction(OP, "equal", var(0), "b", "false"),
                    createInstruction(SET, "a", var(0))
            );
        }
    }
}
