package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class PrintMergerTest  extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            getCompilerProfile(),
            Optimization.INPUT_TEMPS_ELIMINATION,
            Optimization.PRINT_TEXT_MERGING
    );

    @Test
    void mergesTwoPrints() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        print("a")
                        print("b")
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"ab\""),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void mergesThreePrints() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        print("a")
                        print("b")
                        print("c")
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"abc\""),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void mergesAllLiterals() {
        generateInto(pipeline,
                (Seq) translateToAst("""
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
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"15.79684A1B700CnullD999\""),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void handlesInterleavedPrints() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                         print("a")
                         print(x)
                         print("c")
                         print("d")
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"a\""),
                        createInstruction(PRINT, "x"),
                        createInstruction(PRINT, "\"cd\""),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void skipsJumps() {
        generateInto(
                pipeline,
                (Seq) translateToAst("""
                        print("a")
                        if x
                          print(x)
                        end
                        print("b")
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
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
                ),
                terminus.getResult()
        );
    }

    @Test
    void mergesAcrossInstructions() {
        generateInto(
                pipeline,
                (Seq) translateToAst("""
                        println("Rate: ", rate, " items/sec")
                        println("Elapsed: ", @time - start, " ms")
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"Rate: \""),
                        createInstruction(PRINT, "rate"),
                        createInstruction(OP, "sub", var(0), "@time", "start"),
                        createInstruction(PRINT, "\" items/sec\\nElapsed: \""),
                        createInstruction(PRINT, var(0)),
                        createInstruction(PRINT, "\" ms\\n\""),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

}