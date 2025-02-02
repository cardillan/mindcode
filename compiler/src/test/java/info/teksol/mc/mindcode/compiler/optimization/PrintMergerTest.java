package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class PrintMergerTest extends AbstractOptimizerTest<PrintMerger> {

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
                createInstruction(FORMAT, ":rate"),
                createInstruction(OP, "sub", tmp(0), "@time", ":start"),
                createInstruction(FORMAT, tmp(0))
        );
    }

    @Test
    void detectsFormatConflicts() {
        assertCompilesTo(
                expectedMessages().add("A string literal precludes using 'format' instruction for print merging."),
                """
                        println($"Rate: $rate items/sec{0}");
                        if rand(1) > 0.5 then
                            format(" ALERT!");
                        else
                            format("");
                        end;
                        println($"Elapsed: $ ms", @time - start);
                        """,
                createInstruction(PRINT, q("Rate: ")),
                createInstruction(PRINT, ":rate"),
                createInstruction(PRINT, q(" items/sec{0}\n")),
                createInstruction(OP, "rand", tmp(0), "1"),
                createInstruction(OP, "greaterThan", tmp(1), tmp(0), "0.5"),
                createInstruction(JUMP, label(0), "equal", tmp(1), "false"),
                createInstruction(FORMAT, q(" ALERT!")),
                createInstruction(SET, tmp(2), q(" ALERT!")),
                createInstruction(JUMP, label(1), "always"),
                createInstruction(LABEL, label(0)),
                createInstruction(FORMAT, q("")),
                createInstruction(SET, tmp(2), q("")),
                createInstruction(LABEL, label(1)),
                createInstruction(OP, "sub", tmp(3), "@time", ":start"),
                createInstruction(PRINT, q("Elapsed: ")),
                createInstruction(PRINT, tmp(3)),
                createInstruction(PRINT, q(" ms\n"))
        );
    }
}