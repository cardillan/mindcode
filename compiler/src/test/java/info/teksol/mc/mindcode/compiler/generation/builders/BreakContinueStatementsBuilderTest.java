package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class BreakContinueStatementsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class BreakStatements {
        @Test
        void compilesBreakInWhileLoop() {
            assertCompilesTo("""
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
            assertCompilesTo("""
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
            assertCompilesTo("""
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
            assertCompilesTo("""
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
        void compilesNestedLoopBreaks() {
            assertCompilesTo("""
                            while a do
                                print(a);
                                while b do
                                    print(b);
                                    if c then
                                        print(c);
                                        break;
                                    end;
                                    print(d);
                                    break;
                                end;
                                print(e);
                                break;
                            end;
                            print(f);
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1002), "equal", "a", "false"),
                    createInstruction(PRINT, "a"),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(JUMP, var(1005), "equal", "b", "false"),
                    createInstruction(PRINT, "b"),
                    createInstruction(JUMP, var(1006), "equal", "c", "false"),
                    createInstruction(PRINT, "c"),
                    createInstruction(JUMP, var(1005), "always"),
                    createInstruction(SET, var(0), "null"),
                    createInstruction(JUMP, var(1007), "always"),
                    createInstruction(LABEL, var(1006)),
                    createInstruction(SET, var(0), "null"),
                    createInstruction(LABEL, var(1007)),
                    createInstruction(PRINT, "d"),
                    createInstruction(JUMP, var(1005), "always"),
                    createInstruction(LABEL, var(1004)),
                    createInstruction(JUMP, var(1003), "always"),
                    createInstruction(LABEL, var(1005)),
                    createInstruction(PRINT, "e"),
                    createInstruction(JUMP, var(1002), "always"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1000), "always"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(PRINT, "f")
            );
        }

        @Test
        void refusesBreaksOutsideLoop() {
            assertGeneratesMessage("'break' statement outside of a do/while/for loop.",
                    """
                            while a do
                                print(a);
                            end;
                            break;
                            """
            );
        }

        @Test
        void compilesBreakFromOuterLoop() {
            assertCompilesTo("""
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
            assertCompilesTo("""
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
            assertCompilesTo("""
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
            assertCompilesTo("""
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
            assertCompilesTo("""
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
            assertCompilesTo("""
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
            assertCompilesTo("""
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
            assertCompilesTo("""
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
