package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class PrintMergerLogic7Test extends AbstractOptimizerTest<PrintMerger> {

    @Override
    protected Class<PrintMerger> getTestedClass() {
        return PrintMerger.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.PRINT_MERGING
        );
    }

    @Override
    protected ProcessorVersion getProcessorVersion() {
        return ProcessorVersion.V7A;
    }

    @Test
    void mergesTwoPrints() {
        assertCompilesTo("""
                        print("a");
                        print("b");
                        """,
                createInstruction(PRINT, q("ab"))
        );
    }

    @Test
    void mergesThreePrints() {
        assertCompilesTo("""
                        print("a");
                        print("b");
                        print("c");
                        """,
                createInstruction(PRINT, q("abc"))
        );
    }

    @Test
    void mergesAllLiterals() {
        assertCompilesTo("""
                        const MAX = 3 * 333;
                        print(15.79684);
                        print("A");
                        print(true);
                        print("B");
                        print(700);
                        print("C");
                        print(null);
                        print(@flare);
                        print("D");
                        print(MAX);
                        """,
                createInstruction(PRINT, q("15.79684A1B700CnullflareD999"))
        );
    }

    @Test
    void handlesInterleavedPrints() {
        assertCompilesTo("""
                         print("a");
                         print(x);
                         print("c");
                         print("d");
                        """,
                createInstruction(PRINT, q("a")),
                createInstruction(PRINT, ":x"),
                createInstruction(PRINT, q("cd"))
        );
    }

    @Test
    void skipsJumps() {
        assertCompilesTo("""
                        print(":a");
                        if x then
                          print(x);
                        end;
                        print(":b");
                        """,
                createInstruction(PRINT, q(":a")),
                createInstruction(JUMP, label(0), "equal", ":x", "false"),
                createInstruction(PRINT, ":x"),
                createInstruction(SET, tmp(1), ":x"),
                createInstruction(JUMP, label(1), "always"),
                createInstruction(LABEL, label(0)),
                createInstruction(SET, tmp(1), "null"),
                createInstruction(LABEL, label(1)),
                createInstruction(PRINT, q(":b"))
        );
    }

    @Test
    void mergesAcrossInstructions() {
        assertCompilesTo("""
                        println("Rate: ", rate, " items/sec");
                        println("Elapsed: ", @time - start, " ms");
                        """,
                createInstruction(PRINT, q("Rate: ")),
                createInstruction(PRINT, ":rate"),
                createInstruction(OP, "sub", tmp(0), "@time", ":start"),
                createInstruction(PRINT, q(" items/sec\nElapsed: ")),
                createInstruction(PRINT, tmp(0)),
                createInstruction(PRINT, q(" ms\n"))
        );
    }


    @Test
    void mergesRemarks() {
        assertCompilesTo("""
                        remark($"Processing: ${}", @mono);
                        remark("bar");
                        """,
                createInstruction(REMARK, q("Processing: mono")),
                createInstruction(REMARK, q("bar"))
        );
    }
}
