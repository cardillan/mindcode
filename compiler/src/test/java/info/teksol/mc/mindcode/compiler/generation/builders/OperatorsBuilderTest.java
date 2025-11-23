package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

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
                            """,
                    createInstruction(OP, "and", var(0), "a", "b"),
                    createInstruction(OP, "land", var(1), "c", "d")
            );
        }

        @Test
        void compilesOrXorOperations() {
            assertCompilesTo("""
                            a | b;
                            c || d;
                            e ^ f;
                            """,
                    createInstruction(OP, "or", var(0), "a", "b"),
                    createInstruction(OP, "or", var(2), "c", "d"),
                    createInstruction(OP, "notEqual", var(1), var(2), "false"),
                    createInstruction(OP, "xor", var(3), "e", "f")
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
        void compilesEqualitiesInTarget7() {
            assertCompilesTo("""
                            #set target = 7;
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
        void compilesEqualitiesInTarget8() {
            assertCompilesTo("""
                            a == b;
                            c != d;
                            e === f;
                            g !== h;
                            """,
                    createInstruction(OP, "equal", tmp(0), ":a", ":b"),
                    createInstruction(OP, "notEqual", tmp(1), ":c", ":d"),
                    createInstruction(OP, "strictEqual", tmp(2), ":e", ":f"),
                    createInstruction(OP, "strictNotEqual", tmp(3), ":g", ":h")
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
        void emulatesUshrInTarget7() {
            assertCompilesTo("""
                            #set target = 7;
                            a >>> b;
                            """,
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(OP, "and", tmp(1), ":b", "63"),
                    createInstruction(JUMP, label(0), "lessThanEq", tmp(1), "0"),
                    createInstruction(OP, "shr", tmp(0), tmp(0), "1"),
                    createInstruction(OP, "and", tmp(0), tmp(0), "0x7FFFFFFFFFFFFFFF"),
                    createInstruction(OP, "sub", tmp(1), tmp(1), "1"),
                    createInstruction(OP, "shr", tmp(0), tmp(0), tmp(1)),
                    createInstruction(LABEL, label(0)));
        }
    }

    @Nested
    class ShortCircuitingOperators {

        @Test
        void compilesAnd() {
            assertCompilesTo("a and b;",
                    createInstruction(JUMP, label(0), "equal", ":a", "false"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(JUMP, label(0), "equal", ":b", "false"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), "true"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(0), "false"),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesOr() {
            assertCompilesTo("a or b;",
                    createInstruction(JUMP, label(2), "notEqual", ":a", "false"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(JUMP, label(2), "notEqual", ":b", "false"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), "true"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(0), "false"),
                    createInstruction(LABEL, label(1))
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
