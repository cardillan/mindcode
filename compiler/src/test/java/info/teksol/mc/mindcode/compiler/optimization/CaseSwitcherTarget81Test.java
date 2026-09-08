package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mc.profile.options.Target;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class CaseSwitcherTarget81Test extends AbstractOptimizerTest<CaseSwitcher> {

    @Override
    protected Class<CaseSwitcher> getTestedClass() {
        return CaseSwitcher.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return Optimization.LIST;
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setGoal(GenerationGoal.SPEED).setTarget(new Target(ProcessorVersion.V8B, ProcessorType.MICRO_PROCESSOR));
    }

    @Nested
    class ValueTranslationTests {
        @Test
        void processesBasicCase() {
            assertCompilesTo("""
                            param input = 0;
                            print(case input
                                when 0 then 'A';
                                when 1 then 'B';
                                when 2 then 'C';
                            end);
                            """,
                    createInstruction(SET, "input", "0"),
                    createInstruction(READ, tmp(0), q("ABC"), "input"),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesExpressionsOnInput() {
            assertCompilesTo("""
                            param p = 0;
                            
                            printchar(case p \\ 10
                                when 0 then 'A';
                                when 1 then 'B';
                            end);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(OP, "idiv", tmp(1), "p", "10"),
                    createInstruction(READ, tmp(0), q("AB"), tmp(1)),
                    createInstruction(PRINTCHAR, tmp(0))
            );
        }

        @Test
        void processesOutputOffsetToNull() {
            assertCompilesTo("""
                            param input = 0;
                            print(case input
                                when 0 then 2;
                                when 1 then 1;
                                when 2 then 0;
                            end);
                            """,
                    createInstruction(SET, "input", "0"),
                    createInstruction(READ, tmp(1), q("210"), "input"),
                    createInstruction(OP, "sub", tmp(2), tmp(1), "48"),
                    createInstruction(SELECT, tmp(0), "strictEqual", tmp(1), "null", "null", tmp(2)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesOutputOffsetToNumber() {
            assertCompilesTo("""
                            param input = 0;
                            print(case input
                                when 0 then 2;
                                when 1 then 1;
                                when 2 then 0;
                                else 4;
                            end);
                            """,
                    createInstruction(SET, "input", "0"),
                    createInstruction(READ, tmp(1), q("210"), "input"),
                    createInstruction(OP, "sub", tmp(2), tmp(1), "48"),
                    createInstruction(SELECT, tmp(0), "strictEqual", tmp(1), "null", "4", tmp(2)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesOutputNull() {
            assertCompilesTo("""
                            param input = 0;
                            print(case input
                                when 0 then null;
                                when 1 then 'A';
                                when 2 then 'B';
                            end);
                            """,
                    createInstruction(SET, "input", "0"),
                    createInstruction(READ, tmp(1), q("CAB"), "input"),
                    createInstruction(SELECT, tmp(0), "equal", tmp(1), "67", "null", tmp(1)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesOutputNullAndOffset() {
            assertCompilesTo("""
                            param input = 0;
                            print(case input
                                when 0 then null;
                                when 1 then 2;
                                when 2 then 1;
                            end);
                            """,
                    createInstruction(SET, "input", "0"),
                    createInstruction(READ, tmp(1), q(""), "input"),
                    createInstruction(SELECT, tmp(0), "equal", tmp(1), "3", "null", tmp(1)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesNonNullElse() {
            assertCompilesTo("""
                            param input = 0;
                            print(case input
                                when 0 then 'a';
                                when 1 then 'b';
                                when 2 then 'c';
                                else 'd';
                            end);
                            """,
                    createInstruction(SET, "input", "0"),
                    createInstruction(READ, tmp(1), q("abc"), "input"),
                    createInstruction(SELECT, tmp(0), "strictEqual", tmp(1), "null", "100", tmp(1)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesNonNullElseAndOffset() {
            assertCompilesTo("""
                            param input = 0;
                            print(case input
                                when 0 then 0;
                                when 1 then 1;
                                when 2 then 2;
                                else 3;
                            end);
                            """,
                    createInstruction(SET, "input", "0"),
                    createInstruction(READ, tmp(1), q("012"), "input"),
                    createInstruction(OP, "sub", tmp(2), tmp(1), "48"),
                    createInstruction(SELECT, tmp(0), "strictEqual", tmp(1), "null", "3", tmp(2)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesNonNullElseAndNUllBranch() {
            assertCompilesTo("""
                            param input = 0;
                            print(case input
                                when 0 then 'a';
                                when 1 then null;
                                when 4 then 'b';
                                else 'c';
                            end);
                            """,
                    createInstruction(SET, "input", "0"),
                    createInstruction(READ, tmp(1), q("adccb"), "input"),
                    createInstruction(SELECT, tmp(2), "equal", tmp(1), "100", "null", tmp(1)),
                    createInstruction(SELECT, tmp(0), "strictEqual", tmp(1), "null", "99", tmp(2)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesNonNullElseNullBranchAndOffset() {
            assertCompilesTo("""
                            #set goal = size;
                            param input = 0;
                            print(case input
                                when 0 then 1;
                                when 1 then null;
                                when 4 then 2;
                                else 0;
                            end);
                            """,
                    createInstruction(SET, "input", "0"),
                    createInstruction(READ, tmp(1), q("13002"), "input"),
                    createInstruction(OP, "sub", tmp(2), tmp(1), "48"),
                    createInstruction(SELECT, tmp(3), "equal", tmp(1), "51", "null", tmp(2)),
                    createInstruction(SELECT, tmp(0), "strictEqual", tmp(1), "null", "0", tmp(3)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesDiscontinuousElseWithOffset() {
            assertCompilesTo("""
                            param input = 0;
                            print(case input
                                when 0, 1, 2 then 0;
                                when 4, 5, 6 then 1;
                            end);
                            """,
                    createInstruction(SET, "input", "0"),
                    createInstruction(READ, tmp(1), q("000!111"), "input"),
                    createInstruction(OP, "sub", tmp(2), tmp(1), "48"),
                    createInstruction(SELECT, tmp(0), "lessThanEq", tmp(1), "33", "null", tmp(2)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesVoids() {
            assertCompilesTo("""
                            volatile a = '0';
                            case a
                                when '0' then a = '1';
                                when '1' then a = '2';
                                when '2' then a = '0';
                            end;
                            """,
                    createInstruction(SET, ".a", "48"),
                    createInstruction(READ, tmp(2), q("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!120"), ".a"),
                    createInstruction(SELECT, ".a", "lessThanEq", tmp(2), "33", ".a", tmp(2))
            );
        }

        @Test
        void processesVoidsWithContent() {
            assertCompilesTo("""
                            #set goal = size;
                            noinit volatile a;
                            case a
                                when @copper then a = @lead;
                                when @lead then a = @metaglass;
                                when @metaglass then a = @copper;
                            end;
                            """,
                    createInstruction(SENSOR, tmp(2), ".a", "@id"),
                    createInstruction(READ, tmp(3), q("120"), tmp(2)),
                    createInstruction(OP, "sub", tmp(4), tmp(3), "48"),
                    createInstruction(LOOKUP, "item", tmp(5), tmp(4)),
                    createInstruction(SELECT, tmp(6), "strictEqual", tmp(2), "null", ".a", tmp(5)),
                    createInstruction(SELECT, ".a", "lessThanEq", tmp(3), "33", ".a", tmp(6))
            );
        }

        @Test
        void processesNullVoidsWithContent() {
            assertCompilesTo("""
                            #set goal = size;
                            noinit volatile a;
                            case a
                                when null then null;
                                when @copper then a = @lead;
                                when @lead then a = @metaglass;
                                when @metaglass then a = @copper;
                                else a = @silicon;
                            end;
                            """,
                    createInstruction(SENSOR, tmp(2), ".a", "@id"),
                    createInstruction(READ, tmp(3), q("120"), tmp(2)),
                    createInstruction(OP, "sub", tmp(4), tmp(3), "48"),
                    createInstruction(SELECT, tmp(5), "strictEqual", tmp(3), "null", "9", tmp(4)),
                    createInstruction(LOOKUP, "item", tmp(6), tmp(5)),
                    createInstruction(SELECT, tmp(7), "strictEqual", tmp(2), "null", ".a", tmp(6)),
                    createInstruction(SELECT, ".a", "equal", tmp(3), "59", ".a", tmp(7))
            );
        }

        @Test
        void processesKeyVoidsWithContent() {
            assertCompilesTo("""
                            #set goal = size;
                            noinit volatile a;
                            case a
                                when @copper then a = @lead;
                                when @lead then null;
                                when @metaglass then a = @copper;
                                else a = @silicon;
                            end;
                            """,
                    createInstruction(SENSOR, tmp(2), ".a", "@id"),
                    createInstruction(READ, tmp(3), q("1;0"), tmp(2)),
                    createInstruction(OP, "sub", tmp(4), tmp(3), "48"),
                    createInstruction(SELECT, tmp(5), "strictEqual", tmp(2), "null", "9", tmp(4)),
                    createInstruction(SELECT, tmp(6), "strictEqual", tmp(3), "null", "9", tmp(5)),
                    createInstruction(LOOKUP, "item", tmp(7), tmp(6)),
                    createInstruction(SELECT, ".a", "equal", tmp(3), "59", ".a", tmp(7))
            );
        }

        @Test
        void processesKeyElseVoidsWithContent() {
            assertCompilesTo("""
                            #set goal = size;
                            noinit volatile a;
                            case a
                                when @copper then a = @lead;
                                when @lead then null;
                                when @metaglass then a = @copper;
                            end;
                            """,
                    createInstruction(SENSOR, tmp(2), ".a", "@id"),
                    createInstruction(READ, tmp(3), q("1!0"), tmp(2)),
                    createInstruction(OP, "sub", tmp(4), tmp(3), "48"),
                    createInstruction(LOOKUP, "item", tmp(5), tmp(4)),
                    createInstruction(SELECT, tmp(6), "strictEqual", tmp(2), "null", ".a", tmp(5)),
                    createInstruction(SELECT, ".a", "lessThanEq", tmp(3), "33", ".a", tmp(6))
            );
        }

        @Test
        void processesMultipleTranslations() {
            assertCompilesTo("""
                            #set target = 8m;
                            #set goal = size;
                            a = b = '0';
                            while true do
                                case a
                                    when '0' then a = 'b'; b = '1';
                                    when '1' then a = 'c'; b = '2';
                                    when '2' then a = 'a'; b = '3';
                                end;
                                printchar(a);
                                printchar(b);
                            end;
                            """,
                    createInstruction(SET, ":b", "48"),
                    createInstruction(SET, ":a", "48"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(READ, tmp(2), q("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!123"), ":a"),
                    createInstruction(SELECT, ":b", "lessThanEq", tmp(2), "33", ":b", tmp(2)),
                    createInstruction(READ, tmp(4), q("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!bca"), ":a"),
                    createInstruction(SELECT, ":a", "lessThanEq", tmp(4), "33", ":a", tmp(4)),
                    createInstruction(PRINTCHAR, ":a"),
                    createInstruction(PRINTCHAR, ":b"),
                    createInstruction(JUMP, label(0), "always")
            );
        }
    }
}
