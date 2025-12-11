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
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(2), "equal", "true", "false"),
                    createInstruction(OP, "add", ":before", ":before", "1"),
                    createInstruction(OP, "greaterThan", tmp(0), ":b", "10"),
                    createInstruction(JUMP, label(3), "equal", tmp(0), "false"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "add", ":after", ":after", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
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
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", ":before", ":before", "1"),
                    createInstruction(OP, "greaterThan", tmp(0), ":b", "10"),
                    createInstruction(JUMP, label(3), "equal", tmp(0), "false"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "add", ":after", ":after", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(0), "notEqual", "true", "false"),
                    createInstruction(LABEL, label(2))
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
                    createInstruction(SET, ":i", "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "lessThan", tmp(0), ":i", "10"),
                    createInstruction(JUMP, label(2), "equal", tmp(0), "false"),
                    createInstruction(OP, "add", ":before", ":before", "1"),
                    createInstruction(OP, "greaterThan", tmp(1), ":b", "10"),
                    createInstruction(JUMP, label(3), "equal", tmp(1), "false"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(SET, tmp(2), "null"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(2), "null"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "add", ":after", ":after", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, tmp(3), ":i"),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
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
                    createInstruction(SET, ":i", "1"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(2), "greaterThan", ":i", "10"),
                    createInstruction(OP, "add", ":before", ":before", "1"),
                    createInstruction(OP, "greaterThan", tmp(0), ":b", "10"),
                    createInstruction(JUMP, label(3), "equal", tmp(0), "false"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "add", ":after", ":after", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
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
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(2), "equal", ":a", "false"),
                    createInstruction(PRINT, ":a"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(JUMP, label(5), "equal", ":b", "false"),
                    createInstruction(PRINT, ":b"),
                    createInstruction(JUMP, label(6), "equal", ":c", "false"),
                    createInstruction(PRINT, ":c"),
                    createInstruction(JUMP, label(5), "always"),
                    createInstruction(SET, tmp(0), "null"),
                    createInstruction(JUMP, label(7), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(SET, tmp(0), "null"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(PRINT, ":d"),
                    createInstruction(JUMP, label(5), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(PRINT, ":e"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(PRINT, ":f")
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
                    createInstruction(LABEL, label(0)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(OP, "add", ":inner", ":inner", "1"),
                    createInstruction(OP, "greaterThan", tmp(0), ":b", "10"),
                    createInstruction(JUMP, label(6), "equal", tmp(0), "false"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(JUMP, label(7), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(JUMP, label(3), "notEqual", "true", "false"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(OP, "add", ":outer", ":outer", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(0), "notEqual", "true", "false"),
                    createInstruction(LABEL, label(2))
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
                    createInstruction(LABEL, label(0)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(OP, "add", ":inner", ":inner", "1"),
                    createInstruction(OP, "greaterThan", tmp(0), ":b", "10"),
                    createInstruction(JUMP, label(6), "equal", tmp(0), "false"),
                    createInstruction(JUMP, label(5), "always"),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(JUMP, label(7), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(JUMP, label(3), "notEqual", "true", "false"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(OP, "add", ":outer", ":outer", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(0), "notEqual", "true", "false"),
                    createInstruction(LABEL, label(2))
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
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(2), "equal", "true", "false"),
                    createInstruction(OP, "add", ":before", ":before", "1"),
                    createInstruction(OP, "greaterThan", tmp(0), ":b", "10"),
                    createInstruction(JUMP, label(3), "equal", tmp(0), "false"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "add", ":after", ":after", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
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
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", ":before", ":before", "1"),
                    createInstruction(OP, "greaterThan", tmp(0), ":b", "10"),
                    createInstruction(JUMP, label(3), "equal", tmp(0), "false"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "add", ":after", ":after", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(0), "notEqual", "true", "false"),
                    createInstruction(LABEL, label(2))

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
                    createInstruction(SET, ":i", "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "lessThan", tmp(0), ":i", "10"),
                    createInstruction(JUMP, label(2), "equal", tmp(0), "false"),
                    createInstruction(OP, "add", ":before", ":before", "1"),
                    createInstruction(OP, "greaterThan", tmp(1), ":b", "10"),
                    createInstruction(JUMP, label(3), "equal", tmp(1), "false"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(SET, tmp(2), "null"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(2), "null"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "add", ":after", ":after", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, tmp(3), ":i"),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
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
                    createInstruction(SET, ":i", "1"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(2), "greaterThan", ":i", "10"),
                    createInstruction(OP, "add", ":before", ":before", "1"),
                    createInstruction(OP, "greaterThan", tmp(0), ":b", "10"),
                    createInstruction(JUMP, label(3), "equal", tmp(0), "false"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "add", ":after", ":after", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2))
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
                    createInstruction(LABEL, label(0)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(OP, "add", ":inner", ":inner", "1"),
                    createInstruction(OP, "greaterThan", tmp(0), ":b", "10"),
                    createInstruction(JUMP, label(6), "equal", tmp(0), "false"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(JUMP, label(7), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(JUMP, label(3), "notEqual", "true", "false"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(OP, "add", ":outer", ":outer", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(0), "notEqual", "true", "false"),
                    createInstruction(LABEL, label(2))
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
                    createInstruction(LABEL, label(0)),
                    createInstruction(LABEL, label(3)),
                    createInstruction(OP, "add", ":inner", ":inner", "1"),
                    createInstruction(OP, "greaterThan", tmp(0), ":b", "10"),
                    createInstruction(JUMP, label(6), "equal", tmp(0), "false"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(JUMP, label(7), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(JUMP, label(3), "notEqual", "true", "false"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(OP, "add", ":outer", ":outer", "1"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(0), "notEqual", "true", "false"),
                    createInstruction(LABEL, label(2))
            );
        }
    }
}
