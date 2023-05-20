package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class PrintMergerTest extends AbstractOptimizerTest<PrintMerger> {

    @Override
    protected Class<PrintMerger> getTestedClass() {
        return PrintMerger.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.PRINT_TEXT_MERGING
        );
    }

    @Test
    void mergesTwoPrints() {
        assertCompilesTo("""
                        print("a")
                        print("b")
                        """,
                createInstruction(PRINT, "\"ab\""),
                createInstruction(END)
        );
    }

    @Test
    void mergesThreePrints() {
        assertCompilesTo("""
                        print("a")
                        print("b")
                        print("c")
                        """,
                createInstruction(PRINT, "\"abc\""),
                createInstruction(END)
        );
    }

    @Test
    void mergesAllLiterals() {
        assertCompilesTo("""
                        const MAX = 3 * 333
                            print(15.79684)
                            print("A")
                            print(true)
                            print("B")
                            print(700)
                            print("C")
                            print(null)
                            print("D")
                            print(MAX)
                            """,
                createInstruction(PRINT, "\"15.79684A1B700CnullD999\""),
                createInstruction(END)
        );
    }

    @Test
    void handlesInterleavedPrints() {
        assertCompilesTo("""
                         print("a")
                         print(x)
                         print("c")
                         print("d")
                        """,
                createInstruction(PRINT, "\"a\""),
                createInstruction(PRINT, "x"),
                createInstruction(PRINT, "\"cd\""),
                createInstruction(END)
        );
    }

    @Test
    void skipsJumps() {
        assertCompilesTo("""
                        print("a")
                        if x
                          print(x)
                        end
                        print("b")
                        """,
                createInstruction(PRINT, "\"a\""),
                createInstruction(JUMP, var(1000), "equal", "x", "false"),
                createInstruction(PRINT, "x"),
                createInstruction(SET, var(1), "x"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, var(1), "null"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "\"b\""),
                createInstruction(END)
        );
    }

    @Test
    void mergesAcrossInstructions() {
        assertCompilesTo("""
                        println("Rate: ", rate, " items/sec")
                        println("Elapsed: ", @time - start, " ms")
                        """,
                createInstruction(PRINT, "\"Rate: \""),
                createInstruction(PRINT, "rate"),
                createInstruction(OP, "sub", var(0), "@time", "start"),
                createInstruction(PRINT, "\" items/sec\\nElapsed: \""),
                createInstruction(PRINT, var(0)),
                createInstruction(PRINT, "\" ms\\n\""),
                createInstruction(END)
        );
    }

}