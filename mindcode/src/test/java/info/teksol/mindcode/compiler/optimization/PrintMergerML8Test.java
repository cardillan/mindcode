package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class PrintMergerML8Test extends AbstractOptimizerTest<PrintMerger> {

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
    void skipsDrawPrint() {
        assertCompilesTo("""
                        print("a");
                        drawPrint(0, 0, center);
                        print("b");
                        """,
                createInstruction(PRINT, q("a")),
                createInstruction(DRAW, "print", "0", "0", "center"),
                createInstruction(PRINT, q("b")),
                createInstruction(END)
        );
    }

    @Test
    void handlesInterleavedPrints() {
        assertCompilesTo("""
                         print("a", x, "c", "d");
                        """,
                createInstruction(PRINT, q("a{0}cd")),
                createInstruction(FORMAT, "x"),
                createInstruction(END)
        );
    }

    @Test
    void optimizesAcrossInstructions() {
        assertCompilesTo("""
                        println($"Rate: $rate items/sec");
                        println($"Elapsed: $ ms", @time - start);
                        """,
                createInstruction(PRINT, q("Rate: {0} items/sec\nElapsed: {0} ms\n")),
                createInstruction(FORMAT, "rate"),
                createInstruction(OP, "sub", var(0), "@time", "start"),
                createInstruction(FORMAT, var(0)),
                createInstruction(END)
        );
    }

    @Test
    void detectsFormatConflicts() {
        assertCompilesTo("""
                        println($"Rate: $rate items/sec{0}");
                        if rand(1) > 0.5 then
                            format(" ALERT!");
                        else
                            format("");
                        end;
                        println($"Elapsed: $ ms", @time - start);
                        """,
                createInstruction(PRINT, q("Rate: ")),
                createInstruction(PRINT, "rate"),
                createInstruction(PRINT, q(" items/sec{0}\n")),
                createInstruction(OP, "rand", var(0), "1"),
                createInstruction(OP, "greaterThan", var(1), var(0), "0.5"),
                createInstruction(JUMP, var(1000), "equal", var(1), "false"),
                createInstruction(FORMAT, q(" ALERT!")),
                createInstruction(SET, var(2), q(" ALERT!")),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(FORMAT, q("")),
                createInstruction(SET, var(2), q("")),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "sub", var(3), "@time", "start"),
                createInstruction(PRINT, q("Elapsed: ")),
                createInstruction(PRINT, var(3)),
                createInstruction(PRINT, q(" ms\n")),
                createInstruction(END)
        );
    }
}