package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

class StatementListsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class AtomicBlocks {
        @Test
        void compilesSimpleAtomicBlock() {
            assertCompilesTo("""
                            atomic
                                i = cell1[0]++;
                            end;
                            print(i);
                            """,
                    createInstruction(WAIT, "0"),
                    createInstruction(READ, tmp(1), "cell1", "0"),
                    createInstruction(SET, tmp(2), tmp(1)),
                    createInstruction(OP, "add", tmp(0), tmp(1), "1"),
                    createInstruction(WRITE, tmp(0), "cell1", "0"),
                    createInstruction(SET, ":i", tmp(2)),
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, ":i")
            );
        }

        @Test
        void compilesNestedBlocks() {
            assertCompilesTo("""
                            atomic
                                print("start");
                                atomic
                                    print("inner");
                                end;
                                print("end");
                            end;
                            """,
                    createInstruction(WAIT, "0"),
                    createInstruction(PRINT, q("start")),
                    createInstruction(PRINT, q("inner")),
                    createInstruction(LABEL, label(1)),
                    createInstruction(PRINT, q("end")),
                    createInstruction(LABEL, label(0))
            );
        }
    }

    @Nested
    class BreakStatements {
        @Test
        void compilesBreakWithImplicitLabel() {
            assertCompilesTo("""
                            begin
                                print("start");
                                if @time > 10 then
                                    break begin;
                                end;
                                print("end");
                            end;
                            """,
                    createInstruction(PRINT, q("start")),
                    createInstruction(OP, "greaterThan", tmp(0), "@time", "10"),
                    createInstruction(JUMP, label(1), "equal", tmp(0), "false"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(SET, tmp(1), "null"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(PRINT, q("end")),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void compilesLabeledBreak() {
            assertCompilesTo("""
                            MainBlock: begin
                                print("start");
                                break MainBlock;
                                print("end");
                            end;
                            """,
                    createInstruction(PRINT, q("start")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(PRINT, q("end")),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void compilesNestedLabeledBreak() {
            assertCompilesTo("""
                            MainBlock: begin
                                print("outer");
                                begin
                                    print("inner 1");
                                    break MainBlock;
                                    print("inner 2");
                                    break begin;
                                    print("inner 3");
                                end;
                            end;
                            """,
                    createInstruction(PRINT, q("outer")),
                    createInstruction(PRINT, q("inner 1")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(PRINT, q("inner 2")),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(PRINT, q("inner 3")),
                    createInstruction(LABEL, label(1)),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void compilesMultipleImplicitBreaks() {
            assertCompilesTo("""
                            #set target = 8m;
                            #set debug = true;
                            
                            begin
                                print("outer");
                                atomic
                                    print("middle");
                                    debug
                                        print("inner 1");
                                        break debug;
                                        print("inner 2");
                                        break begin;
                                        print("inner 3");
                                        break atomic;
                                        print("inner 4");
                                    end;
                                end;
                            end;
                            
                            """,
                    createInstruction(PRINT, q("outer")),
                    createInstruction(WAIT, "0"),
                    createInstruction(PRINT, q("middle")),
                    createInstruction(PRINT, q("inner 1")),
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(PRINT, q("inner 2")),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(PRINT, q("inner 3")),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(PRINT, q("inner 4")),
                    createInstruction(LABEL, label(2)),
                    createInstruction(LABEL, label(1)),
                    createInstruction(LABEL, label(0))
            );
        }

        @Test
        void detectsAmbiguousImplicitLabels() {
            assertGeneratesMessage(
                    "Ambiguous label 'begin'.",
                    """
                            begin
                                print("outer");
                                begin
                                    print("inner");
                                    break begin;
                                end;
                            end;
                            """);
        }
    }
}
