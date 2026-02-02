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
                    createInstruction(PRINT, q("end"))
            );
        }
    }
}
