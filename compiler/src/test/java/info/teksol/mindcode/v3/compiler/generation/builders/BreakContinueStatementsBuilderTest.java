package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.v3.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class BreakContinueStatementsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class BreakStatements {
        @Test
        void compilesBreakInWhileLoop() {
            assertCompiles("""
                            while true do
                                ++before;
                                if b > 10 then break; end;
                                ++after;
                            end;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1003), "equal", "true", "false"),
                    createInstruction(OP, "add", "before", "before", "1"),
                    createInstruction(OP, "greaterThan", var(0), "b", "10"),
                    createInstruction(JUMP, var(1004), "equal", var(0), "false"),
                    createInstruction(JUMP, var(1003), "always"),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(JUMP, var(1005), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(OP, "add", "after", "after", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesBreakInDoWhileLoop() {
            assertCompiles("""
                            do
                                ++before;
                                if b > 10 then break; end;
                                ++after;
                            while true;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "add", "before", "before", "1"),
                    createInstruction(OP, "greaterThan", var(0), "b", "10"),
                    createInstruction(JUMP, var(1004), "equal", var(0), "false"),
                    createInstruction(JUMP, var(1003), "always"),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(JUMP, var(1005), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(OP, "add", "after", "after", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1001), "notEqual", "true", "false"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesBreakIteratedForLoop() {
            assertCompiles("""
                            for i = 0; i < 10; i++ do
                                ++before;
                                if b > 10 then break; end;
                                ++after;
                            end;
                            """,
                    createInstruction(SET, "i", "0"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "lessThan", var(0), "i", "10"),
                    createInstruction(JUMP, var(1003), "equal", var(0), "false"),
                    createInstruction(OP, "add", "before", "before", "1"),
                    createInstruction(OP, "greaterThan", var(1), "b", "10"),
                    createInstruction(JUMP, var(1004), "equal", var(1), "false"),
                    createInstruction(JUMP, var(1003), "always"),
                    createInstruction(SET, var(2), "null"),
                    createInstruction(JUMP, var(1005), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(2), "null"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(OP, "add", "after", "after", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(3), "i"),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesBreakRangedForLoop() {
            assertCompiles("""
                            for i in 1 .. 10 do
                                ++before;
                                if b > 10 then break; end;
                                ++after;
                            end;
                            """,
                    createInstruction(SET, "i", "1"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1003), "greaterThan", "i", "10"),
                    createInstruction(OP, "add", "before", "before", "1"),
                    createInstruction(OP, "greaterThan", var(0), "b", "10"),
                    createInstruction(JUMP, var(1004), "equal", var(0), "false"),
                    createInstruction(JUMP, var(1003), "always"),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(JUMP, var(1005), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(OP, "add", "after", "after", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesBreakFromOuterLoop() {
            assertCompiles("""
                            outer: do
                                inner: do
                                    ++inner;
                                    if b > 10 then break outer; end;
                                while true;
                                ++outer;
                            while true;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(OP, "add", "inner", "inner", "1"),
                    createInstruction(OP, "greaterThan", var(0), "b", "10"),
                    createInstruction(JUMP, var(1007), "equal", var(0), "false"),
                    createInstruction(JUMP, var(1003), "always"),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(JUMP, var(1008), "always"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(LABEL, var(1008)),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(JUMP, var(1004), "notEqual", "true", "false"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(OP, "add", "outer", "outer", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1001), "notEqual", "true", "false"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesBreakFromInnerLoop() {
            assertCompiles("""
                            outer: do
                                inner: do
                                    ++inner;
                                    if b > 10 then break inner; end;
                                while true;
                                ++outer;
                            while true;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(OP, "add", "inner", "inner", "1"),
                    createInstruction(OP, "greaterThan", var(0), "b", "10"),
                    createInstruction(JUMP, var(1007), "equal", var(0), "false"),
                    createInstruction(JUMP, var(1006), "always"),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(JUMP, var(1008), "always"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(LABEL, var(1008)),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(JUMP, var(1004), "notEqual", "true", "false"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(OP, "add", "outer", "outer", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1001), "notEqual", "true", "false"),
                    createInstruction(LABEL, var(1003))
            );
        }
    }

    @Nested
    class ContinueStatements {
        @Test
        void compilesContinueInWhileLoop() {
            assertCompiles("""
                            while true do
                                ++before;
                                if b > 10 then continue; end;
                                ++after;
                            end;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1003), "equal", "true", "false"),
                    createInstruction(OP, "add", "before", "before", "1"),
                    createInstruction(OP, "greaterThan", var(0), "b", "10"),
                    createInstruction(JUMP, var(1004), "equal", var(0), "false"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(JUMP, var(1005), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(OP, "add", "after", "after", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesContinueInDoWhileLoop() {
            assertCompiles("""
                            do
                                ++before;
                                if b > 10 then continue; end;
                                ++after;
                            while true;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "add", "before", "before", "1"),
                    createInstruction(OP, "greaterThan", var(0), "b", "10"),
                    createInstruction(JUMP, var(1004), "equal", var(0), "false"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(JUMP, var(1005), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(OP, "add", "after", "after", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1001), "notEqual", "true", "false"),
                    createInstruction(LABEL, var(1003))

            );
        }

        @Test
        void compilesContinueIteratedForLoop() {
            assertCompiles("""
                            for i = 0; i < 10; i++ do
                                ++before;
                                if b > 10 then continue; end;
                                ++after;
                            end;
                            """,
                    createInstruction(SET, "i", "0"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(OP, "lessThan", var(0), "i", "10"),
                    createInstruction(JUMP, var(1003), "equal", var(0), "false"),
                    createInstruction(OP, "add", "before", "before", "1"),
                    createInstruction(OP, "greaterThan", var(1), "b", "10"),
                    createInstruction(JUMP, var(1004), "equal", var(1), "false"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(SET, var(2), "null"),
                    createInstruction(JUMP, var(1005), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(2), "null"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(OP, "add", "after", "after", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(SET, var(3), "i"),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesContinueRangedForLoop() {
            assertCompiles("""
                            for i in 1 .. 10 do
                                ++before;
                                if b > 10 then continue; end;
                                ++after;
                            end;
                            """,
                    createInstruction(SET, "i", "1"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1003), "greaterThan", "i", "10"),
                    createInstruction(OP, "add", "before", "before", "1"),
                    createInstruction(OP, "greaterThan", var(0), "b", "10"),
                    createInstruction(JUMP, var(1004), "equal", var(0), "false"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(JUMP, var(1005), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(OP, "add", "after", "after", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(OP, "add", "i", "i", "1"),
                    createInstruction(JUMP, var(1001), "always"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesContinueFromOuterLoop() {
            assertCompiles("""
                            outer: do
                                inner: do
                                    ++inner;
                                    if b > 10 then continue outer; end;
                                while true;
                                ++outer;
                            while true;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(OP, "add", "inner", "inner", "1"),
                    createInstruction(OP, "greaterThan", var(0), "b", "10"),
                    createInstruction(JUMP, var(1007), "equal", var(0), "false"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(JUMP, var(1008), "always"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(LABEL, var(1008)),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(JUMP, var(1004), "notEqual", "true", "false"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(OP, "add", "outer", "outer", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1001), "notEqual", "true", "false"),
                    createInstruction(LABEL, var(1003))
            );
        }

        @Test
        void compilesContinueFromInnerLoop() {
            assertCompiles("""
                            outer: do
                                inner: do
                                    ++inner;
                                    if b > 10 then continue inner; end;
                                while true;
                                ++outer;
                            while true;
                            """,
                    createInstruction(LABEL, var(1001)),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(OP, "add", "inner", "inner", "1"),
                    createInstruction(OP, "greaterThan", var(0), "b", "10"),
                    createInstruction(JUMP, var(1007), "equal", var(0), "false"),
                    createInstruction(JUMP, var(1005), "always"),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(JUMP, var(1008), "always"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(SET, var(1), "null"),
                    createInstruction(LABEL, var(1008)),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(JUMP, var(1004), "notEqual", "true", "false"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(OP, "add", "outer", "outer", "1"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(JUMP, var(1001), "notEqual", "true", "false"),
                    createInstruction(LABEL, var(1003))
            );
        }
    }
}