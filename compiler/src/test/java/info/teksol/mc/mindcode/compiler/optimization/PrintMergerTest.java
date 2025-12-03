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
                        drawPrint(0, 0, @center);
                        print("b");
                        """,
                createInstruction(PRINT, q("a")),
                createInstruction(DRAW, "print", "0", "0", "@center"),
                createInstruction(PRINT, q("b"))
        );
    }

    @Test
    void handlesInterleavedPrints() {
        assertCompilesTo("""
                         print("a", x, "c", "d");
                        """,
                createInstruction(PRINT, q("a{0}cd")),
                createInstruction(FORMAT, ":x")
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

    @Test
    void optimizesPrintChar() {
        assertCompilesTo("""
                        printchar(65);
                        printchar(66);
                        print("C");
                        """,
                createInstruction(PRINT, q("ABC"))
        );
    }

    @Test
    void optimizesPrintCharNoFormat() {
        assertCompilesTo(
                expectedMessages().add("A string literal precludes using 'format' instruction for print merging."),
                """
                        param x = "{0}";
                        printchar(65);
                        printchar(66);
                        print("C");
                        """,
                createInstruction(SET, "x", q("{0}")),
                createInstruction(PRINT, q("ABC"))
        );
    }

    @Test
    void skipsNonConstantPrintChar() {
        assertCompilesTo("""
                        param ch = 65;
                        print("a");
                        printchar(ch);
                        print("b");
                        """,
                createInstruction(SET, "ch", "65"),
                createInstruction(PRINT, q("a")),
                createInstruction(PRINTCHAR, "ch"),
                createInstruction(PRINT, q("b"))
        );
    }

    @Test
    void skipsNonRepresentablesInPrintChar() {
        assertCompilesTo("""
                        printchar(30);
                        printchar(31);
                        printchar(32);
                        printchar(33);
                        printchar(34);
                        printchar(35);
                        printchar(36);
                        """,
                createInstruction(PRINTCHAR, "30"),
                createInstruction(PRINTCHAR, "31"),
                createInstruction(PRINT, q(" !")),
                createInstruction(PRINTCHAR, "34"),
                createInstruction(PRINT, q("#$"))
        );
    }

    @Test
    void skipsFunctionCalls() {
        assertCompilesTo("""
                        #set symbolic-labels = true;
                        noinline void foo()
                            println("In");
                        end;
                        
                        println("Out");
                        foo();
                        println("Out");
                        """,
                createInstruction(PRINT, q("Out\n")),
                createInstruction(CALL, label(0), ":foo*retaddr", ":foo*retval"),
                createInstruction(PRINT, q("Out\n")),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(COMMENT, q("Function: noinline void foo()")),
                createInstruction(PRINT, q("In\n")),
                createInstruction(RETURN, ":foo*retaddr")
        );
    }

    @Test
    void observesLocalOptions() {
        // In target 8, print merging by format is active, which doesn't have the limit.
        assertCompilesTo("""
                        #set target = 7;
                        println("This sentence is longer than 34 characters");
                        #setlocal print-merging = basic;
                        println("This sentence is also longer than 34 characters");
                        """,
                createInstruction(PRINT, q("This sentence is longer than 34 characters\n")),
                createInstruction(PRINT, q("This sentence is also longer than 34 characters")),
                createInstruction(PRINT, q("\n"))
        );
    }
}
