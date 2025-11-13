package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class CaseSwitcherTest extends AbstractOptimizerTest<CaseSwitcher> {

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
        return super.createCompilerProfile().setGoal(GenerationGoal.SPEED);
    }

    @Nested
    class CaseSwitcherTests {
        @Test
        void processesBasicSwitch() {
            assertCompilesTo("""
                            #set case-optimization-strength = 0;
                            #set use-text-jump-tables = false;
                            i = rand(10);
                            case i
                                when 0 then print(0);
                                when 1 then print(1);
                                when 2 then print(2);
                                when 3 then print(3);
                                when 4 then print(4);
                                when 5 then print(5);
                                when 6 then print(6);
                                when 7 then print(7);
                                when 8 then print(8);
                                when 9 then print(9);
                                else print("oh no!");
                            end;
                            print("end");
                            """,
                    createInstruction(OP, "rand", ":i", "10"),
                    createInstruction(JUMP, label(21), "lessThan", ":i", "0"),
                    createInstruction(JUMP, label(21), "greaterThanEq", ":i", "10"),
                    createInstruction(MULTIJUMP, label(22), ":i", "0"),
                    createInstruction(MULTILABEL, label(22)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(23)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(24)),
                    createInstruction(JUMP, label(6), "always"),
                    createInstruction(MULTILABEL, label(25)),
                    createInstruction(JUMP, label(8), "always"),
                    createInstruction(MULTILABEL, label(26)),
                    createInstruction(JUMP, label(10), "always"),
                    createInstruction(MULTILABEL, label(27)),
                    createInstruction(JUMP, label(12), "always"),
                    createInstruction(MULTILABEL, label(28)),
                    createInstruction(JUMP, label(14), "always"),
                    createInstruction(MULTILABEL, label(29)),
                    createInstruction(JUMP, label(16), "always"),
                    createInstruction(MULTILABEL, label(30)),
                    createInstruction(JUMP, label(18), "always"),
                    createInstruction(MULTILABEL, label(31)),
                    createInstruction(PRINT, "9"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(PRINT, "0"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(PRINT, "1"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(PRINT, "2"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(8)),
                    createInstruction(PRINT, "3"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(10)),
                    createInstruction(PRINT, "4"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(12)),
                    createInstruction(PRINT, "5"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(14)),
                    createInstruction(PRINT, "6"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(16)),
                    createInstruction(PRINT, "7"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(18)),
                    createInstruction(PRINT, "8"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(21)),
                    createInstruction(PRINT, q("oh no!")),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, q("end"))
            );
        }

        @Test
        void processesBasicSwitchNoElse() {
            assertCompilesTo("""
                            #set case-optimization-strength = 0;
                            #set use-text-jump-tables = false;
                            i = rand(10);
                            case i
                                when 0 then print(0);
                                when 1 then print(1);
                                when 2 then print(2);
                                when 3 then print(3);
                                when 4 then print(4);
                                when 5 then print(5);
                                when 6 then print(6);
                                when 7 then print(7);
                                when 8 then print(8);
                                when 9 then print(9);
                            end;
                            print("end");
                            """,
                    createInstruction(OP, "rand", ":i", "10"),
                    createInstruction(JUMP, label(21), "lessThan", ":i", "0"),
                    createInstruction(JUMP, label(21), "greaterThanEq", ":i", "10"),
                    createInstruction(MULTIJUMP, label(22), ":i", "0"),
                    createInstruction(MULTILABEL, label(22)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(23)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(24)),
                    createInstruction(JUMP, label(6), "always"),
                    createInstruction(MULTILABEL, label(25)),
                    createInstruction(JUMP, label(8), "always"),
                    createInstruction(MULTILABEL, label(26)),
                    createInstruction(JUMP, label(10), "always"),
                    createInstruction(MULTILABEL, label(27)),
                    createInstruction(JUMP, label(12), "always"),
                    createInstruction(MULTILABEL, label(28)),
                    createInstruction(JUMP, label(14), "always"),
                    createInstruction(MULTILABEL, label(29)),
                    createInstruction(JUMP, label(16), "always"),
                    createInstruction(MULTILABEL, label(30)),
                    createInstruction(JUMP, label(18), "always"),
                    createInstruction(MULTILABEL, label(31)),
                    createInstruction(JUMP, label(20), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(PRINT, "0"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(PRINT, "1"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(PRINT, "2"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(8)),
                    createInstruction(PRINT, "3"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(10)),
                    createInstruction(PRINT, "4"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(12)),
                    createInstruction(PRINT, "5"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(14)),
                    createInstruction(PRINT, "6"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(16)),
                    createInstruction(PRINT, "7"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(18)),
                    createInstruction(PRINT, "8"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(20)),
                    createInstruction(PRINT, "9"),
                    createInstruction(LABEL, label(21)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, q("end"))
            );
        }

        @Test
        void processesReturningSwitchWithElse() {
            assertCompilesTo("""
                            #set case-optimization-strength = 0;
                            #set use-text-jump-tables = false;
                            value = floor(rand(20));
                            text = case value
                                when 0 then "None";
                                when 1 then "One";
                                when 2, 3, 4 then "A few";
                                when 5, 6, 7, 8 then "Several";
                                when 10 then "Ten";
                                else "I don't known this number!";
                            end;
                            print(text);
                            """,
                    createInstruction(OP, "rand", tmp(0), "20"),
                    createInstruction(OP, "floor", ":value", tmp(0)),
                    createInstruction(JUMP, label(11), "lessThan", ":value", "0"),
                    createInstruction(JUMP, label(11), "greaterThanEq", ":value", "11"),
                    createInstruction(MULTIJUMP, label(12), ":value", "0"),
                    createInstruction(MULTILABEL, label(12)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(13)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(14)),
                    createInstruction(JUMP, label(6), "always"),
                    createInstruction(MULTILABEL, label(15)),
                    createInstruction(JUMP, label(6), "always"),
                    createInstruction(MULTILABEL, label(16)),
                    createInstruction(JUMP, label(6), "always"),
                    createInstruction(MULTILABEL, label(17)),
                    createInstruction(JUMP, label(8), "always"),
                    createInstruction(MULTILABEL, label(18)),
                    createInstruction(JUMP, label(8), "always"),
                    createInstruction(MULTILABEL, label(19)),
                    createInstruction(JUMP, label(8), "always"),
                    createInstruction(MULTILABEL, label(20)),
                    createInstruction(JUMP, label(8), "always"),
                    createInstruction(MULTILABEL, label(21)),
                    createInstruction(JUMP, label(11), "always"),
                    createInstruction(MULTILABEL, label(22)),
                    createInstruction(SET, tmp(2), q("Ten")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(2), q("None")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(2), q("One")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(SET, tmp(2), q("A few")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(8)),
                    createInstruction(SET, tmp(2), q("Several")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(11)),
                    createInstruction(SET, tmp(2), q("I don't known this number!")),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(2))
            );
        }

        @Test
        void processesUnsafeCaseExpression() {
            assertCompilesTo("""
                            #set unsafe-case-optimization = true;
                            #set use-text-jump-tables = false;
                            param p = 0;
                            print(case p
                                when 0 then "0";
                                when 1 then "1";
                                when 2 then "2";
                                when 3 then "3";
                            end);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(MULTIJUMP, label(10), "p", "0"),
                    createInstruction(MULTILABEL, label(10)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(11)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(12)),
                    createInstruction(JUMP, label(6), "always"),
                    createInstruction(MULTILABEL, label(13)),
                    createInstruction(JUMP, label(8), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), q("0")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), q("1")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(SET, tmp(0), q("2")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(8)),
                    createInstruction(SET, tmp(0), q("3")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void avoidsUnsafeCaseExpressionWithElse() {
            assertCompilesTo("""
                            #set null-counter-is-noop = false;
                            #set unsafe-case-optimization = true;
                            param p = 0;
                            print(case p
                                when 0 then "0";
                                when 1 then "1";
                                when 2 then "2";
                                when 3 then "3";
                                else 4;
                            end);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(JUMP, label(1), "notEqual", "p", "0"),
                    createInstruction(SET, tmp(0), q("0")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(3), "notEqual", "p", "1"),
                    createInstruction(SET, tmp(0), q("1")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(JUMP, label(5), "notEqual", "p", "2"),
                    createInstruction(SET, tmp(0), q("2")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(JUMP, label(7), "notEqual", "p", "3"),
                    createInstruction(SET, tmp(0), q("3")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(SET, tmp(0), "4"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesCaseExpressionWithSymbolicLabelsPadding() {
            assertCompilesTo("""
                            #set symbolic-labels = true;
                            #set unsafe-case-optimization = true;
                            param p = 3;
                            print(case p
                                when 3 then "A";
                                when 4 then "B";
                                when 5 then "C";
                                when 6 then "D";
                            end);
                            """,
                    createInstruction(SET, "p", "3"),
                    createInstruction(MULTIJUMP, label(10), "p", "0"),
                    createInstruction(MULTILABEL, label(10)),
                    createInstruction(JUMP, label(9), "always"),
                    createInstruction(MULTILABEL, label(11)),
                    createInstruction(JUMP, label(9), "always"),
                    createInstruction(MULTILABEL, label(12)),
                    createInstruction(JUMP, label(9), "always"),
                    createInstruction(MULTILABEL, label(13)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(14)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(15)),
                    createInstruction(JUMP, label(6), "always"),
                    createInstruction(MULTILABEL, label(16)),
                    createInstruction(JUMP, label(8), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), q("A")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(SET, tmp(0), q("C")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(8)),
                    createInstruction(SET, tmp(0), q("D")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(9)),
                    createInstruction(SET, tmp(0), "null"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesOffsetCaseExpressionWithSymbolicLabels() {
            assertCompilesTo("""
                            #set symbolic-labels = true;
                            #set unsafe-case-optimization = true;
                            #set instruction-limit = 18;
                            param p = 5;
                            print(case p
                                when 4, 6, 8 then "A";
                                when 5, 7, 9 then "B";
                            end);
                            """,
                    createInstruction(SET, "p", "5"),
                    createInstruction(MULTIJUMP, label(6), "p", "4"),
                    createInstruction(MULTILABEL, label(6)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(7)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(8)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(9)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(10)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(11)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), q("A")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesItemCaseExpression() {
            assertCompilesTo("""
                            #set use-text-jump-tables = false;
                            param p = @coal;
                            print(case p
                                when @copper,
                                     @metaglass,
                                     @titanium,
                                     @scrap then "A";
                                when @lead,
                                     @graphite,
                                     @thorium,
                                     @silicon then "B";
                                else "C";
                            end);
                            """,
                    createInstruction(SET, "p", "@coal"),
                    createInstruction(SENSOR, tmp(1), "p", "@id"),
                    createInstruction(JUMP, label(5), "greaterThanEq", tmp(1), "10"),
                    createInstruction(MULTIJUMP, label(7), tmp(1), "0"),
                    createInstruction(MULTILABEL, label(7)),
                    createInstruction(JUMP, label(6), "always"),
                    createInstruction(MULTILABEL, label(8)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(9)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(10)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(11)),
                    createInstruction(JUMP, label(5), "always"),
                    createInstruction(MULTILABEL, label(12)),
                    createInstruction(JUMP, label(5), "always"),
                    createInstruction(MULTILABEL, label(13)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(14)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(15)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(16)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(JUMP, label(5), "strictEqual", tmp(1), "null"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), q("A")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(SET, tmp(0), q("C")),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesSplitJumpTable() {
            assertCompilesTo("""
                            #set instruction-limit = 60;
                            #set use-text-jump-tables = false;
                            param p = 0;
                            print(case p
                                when 0, 2, 4, 6, 50, 52, 54, 56 then "A";
                                when 1, 3, 5, 7, 51, 53, 55, 57 then "B";
                            end);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(JUMP, label(6), "greaterThanEq", "p", "8"),
                    createInstruction(JUMP, label(7), "greaterThanEq", "p", "1"),
                    createInstruction(JUMP, label(5), "lessThan", "p", "0"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), q("A")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(MULTIJUMP, label(8), "p", "1"),
                    createInstruction(MULTILABEL, label(8)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(9)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(10)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(11)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(12)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(13)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(14)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(JUMP, label(5), "lessThan", "p", "50"),
                    createInstruction(JUMP, label(5), "greaterThanEq", "p", "58"),
                    createInstruction(MULTIJUMP, label(16), "p", "50"),
                    createInstruction(MULTILABEL, label(16)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(17)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(18)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(19)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(20)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(21)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(22)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(23)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(SET, tmp(0), "null"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesSplitJumpTableWithSymbolicLabels() {
            assertCompilesTo("""
                            #set instruction-limit = 60;
                            #set symbolic-labels = true;
                            param p = 0;
                            print(case p
                                when 0, 2, 4, 6, 8, 50, 52, 54, 56, 58, 60 then "A";
                                when 1, 3, 5, 7, 9, 51, 53, 55, 57, 59, 61 then "B";
                            end);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(JUMP, label(6), "greaterThanEq", "p", "50"),
                    createInstruction(JUMP, label(5), "greaterThanEq", "p", "10"),
                    createInstruction(JUMP, label(5), "lessThan", "p", "0"),
                    createInstruction(MULTIJUMP, label(7), "p", "0"),
                    createInstruction(MULTILABEL, label(7)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(8)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(9)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(10)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(11)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(12)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(13)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(14)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(15)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(16)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(JUMP, label(5), "greaterThanEq", "p", "62"),
                    createInstruction(MULTIJUMP, label(18), "p", "50"),
                    createInstruction(MULTILABEL, label(18)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(19)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(20)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(21)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(22)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(23)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(24)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(25)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(26)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(27)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(28)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(29)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), q("A")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(SET, tmp(0), "null"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void processesCompressedSplitJumpTable() {
            assertCompilesTo("""
                            #set instruction-limit = 60;
                            #set use-text-jump-tables = false;
                            param p = 0;
                            print(case p
                                when 0, 2, 4, 6, 8, 50, 52 then "A";
                                when 1, 3, 5, 7, 9, 51     then "B";
                            end);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(JUMP, label(6), "greaterThanEq", "p", "10"),
                    createInstruction(JUMP, label(7), "greaterThanEq", "p", "1"),
                    createInstruction(JUMP, label(5), "lessThan", "p", "0"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, tmp(0), q("A")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(MULTIJUMP, label(8), "p", "1"),
                    createInstruction(MULTILABEL, label(8)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(9)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(10)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(11)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(12)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(13)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(14)),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(MULTILABEL, label(15)),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(MULTILABEL, label(16)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(6)),
                    createInstruction(JUMP, label(5), "greaterThanEq", "p", "53"),
                    createInstruction(JUMP, label(2), "equal", "p", "50"),
                    createInstruction(JUMP, label(4), "equal", "p", "51"),
                    createInstruction(JUMP, label(2), "equal", "p", "52"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(SET, tmp(0), "null"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void avoidsTooLargeExpressions() {
            assertCompilesTo("""
                            #set unsafe-case-optimization = true;
                            param p = 0;
                            print(case p
                                when 0 then "0";
                                when 1 then "1";
                                when 2 then "2";
                                when 1000 then "1000";
                            end);
                            """,
                    createInstruction(SET, "p", "0"),
                    createInstruction(JUMP, label(1), "notEqual", "p", "0"),
                    createInstruction(SET, tmp(0), q("0")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(JUMP, label(3), "notEqual", "p", "1"),
                    createInstruction(SET, tmp(0), q("1")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(JUMP, label(5), "notEqual", "p", "2"),
                    createInstruction(SET, tmp(0), q("2")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(JUMP, label(7), "notEqual", "p", "1000"),
                    createInstruction(SET, tmp(0), q("1000")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(7)),
                    createInstruction(SET, tmp(0), "null"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }
    }

    @Nested
    class FastDispatchTests {
        @Test
        void processesBasicCase() {
            assertCompilesTo("""
                            param input = 0;
                            print(case input
                                when 0 then "A";
                                when 1 then "B";
                                when 2 then "C";
                                else "D";
                            end);
                            """,
                    createInstruction(SET, "input", "0"),
                    createInstruction(MULTIJUMP, "input", "0", "0"),
                    createInstruction(MULTILABEL, label(12)),
                    createInstruction(SET, tmp(0), q("D")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(9)),
                    createInstruction(SET, tmp(0), q("A")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(10)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(11)),
                    createInstruction(SET, tmp(0), q("C")),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void contentNoNullOrZero() {
            assertCompilesTo("""
                            param input = @water;
                            print(case input
                                when @slag, @oil, @cryofluid then "A";
                                when @gallium, @hydrogen, @nitrogen then "B";
                                when @ozone, @cyanogen then "C";
                                else "E";
                            end);
                            """,
                    createInstruction(SET, "input", "@water"),
                    createInstruction(SENSOR, tmp(1), "input", "@id"),
                    createInstruction(MULTIJUMP, tmp(1), "0", "0"),
                    createInstruction(MULTILABEL, label(13)),
                    createInstruction(MULTILABEL, label(9)),
                    createInstruction(SET, tmp(0), q("E")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(11)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(12)),
                    createInstruction(SET, tmp(0), q("C")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(10)),
                    createInstruction(SET, tmp(0), q("A")),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void contentNullNoZero() {
            assertCompilesTo("""
                            param input = @water;
                            print(case input
                                when @slag, @oil, @cryofluid then "A";
                                when @gallium, @hydrogen, @nitrogen then "B";
                                when @ozone, @cyanogen then "C";
                                when null then "D";
                                else "E";
                            end);
                            """,
                    createInstruction(SET, "input", "@water"),
                    createInstruction(SENSOR, tmp(1), "input", "@id"),
                    createInstruction(MULTIJUMP, tmp(1), "0", "0"),
                    createInstruction(MULTILABEL, label(16)),
                    createInstruction(MULTILABEL, label(13)),
                    createInstruction(LABEL, label(9)),
                    createInstruction(SET, tmp(0), q("E")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(14)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(15)),
                    createInstruction(SET, tmp(0), q("C")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(11)),
                    createInstruction(JUMP, label(9), "strictEqual", tmp(1), "0"),
                    createInstruction(SET, tmp(0), q("D")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(12)),
                    createInstruction(SET, tmp(0), q("A")),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void contentZeroNoNull() {
            assertCompilesTo("""
                            param input = @water;
                            print(case input
                                when @water, @slag, @oil, @cryofluid then "A";
                                when @gallium, @hydrogen, @nitrogen then "B";
                                when @ozone, @cyanogen then "C";
                                else "E";
                            end);
                            """,
                    createInstruction(SET, "input", "@water"),
                    createInstruction(SENSOR, tmp(1), "input", "@id"),
                    createInstruction(MULTIJUMP, tmp(1), "0", "0"),
                    createInstruction(MULTILABEL, label(15)),
                    createInstruction(MULTILABEL, label(12)),
                    createInstruction(LABEL, label(7)),
                    createInstruction(SET, tmp(0), q("E")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(13)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(14)),
                    createInstruction(SET, tmp(0), q("C")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(10)),
                    createInstruction(JUMP, label(7), "strictEqual", tmp(1), "null"),
                    createInstruction(MULTILABEL, label(11)),
                    createInstruction(SET, tmp(0), q("A")),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }

        @Test
        void contentNullAndZero() {
            assertCompilesTo("""
                            param input = @water;
                            print(case input
                                when @water, @slag, @oil, @cryofluid then "A";
                                when @gallium, @hydrogen, @nitrogen then "B";
                                when @ozone, @cyanogen then "C";
                                when null then "D";
                                else "E";
                            end);
                            """,
                    createInstruction(SET, "input", "@water"),
                    createInstruction(SENSOR, tmp(1), "input", "@id"),
                    createInstruction(MULTIJUMP, tmp(1), "0", "0"),
                    createInstruction(MULTILABEL, label(17)),
                    createInstruction(MULTILABEL, label(14)),
                    createInstruction(SET, tmp(0), q("E")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(15)),
                    createInstruction(SET, tmp(0), q("B")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(16)),
                    createInstruction(SET, tmp(0), q("C")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(8)),
                    createInstruction(SET, tmp(0), q("D")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(MULTILABEL, label(12)),
                    createInstruction(JUMP, label(8), "strictEqual", tmp(1), "null"),
                    createInstruction(MULTILABEL, label(13)),
                    createInstruction(SET, tmp(0), q("A")),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, tmp(0))
            );
        }
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
                    createInstruction(READ, tmp(1), q("000 111"), "input"),
                    createInstruction(OP, "sub", tmp(2), tmp(1), "48"),
                    createInstruction(SELECT, tmp(0), "lessThanEq", tmp(1), "32", "null", tmp(2)),
                    createInstruction(PRINT, tmp(0))
            );
        }
    }
}
