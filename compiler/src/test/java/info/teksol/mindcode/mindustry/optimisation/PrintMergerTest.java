package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.generator.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

public class PrintMergerTest  extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = Optimisation.createPipelineOf(terminus, 
            Optimisation.INPUT_TEMPS_ELIMINATION,
            Optimisation.PRINT_TEXT_MERGING
    );

    @Test
    void mergesTwoPrints() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "print(\"a\")\n" +
                        "print(\"b\")"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(PRINT, "\"ab\""),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void mergesThreePrints() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "print(\"a\")\n" +
                        "print(\"b\")\n" +
                        "print(\"c\")"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(PRINT, "\"abc\""),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void handlesInterleavedPrints() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "print(\"a\")\n" +
                        "print(x)\n" +
                        "print(\"c\")" +
                        "print(\"d\")"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(PRINT, "\"a\""),
                        new LogicInstruction(PRINT, "x"),
                        new LogicInstruction(PRINT, "\"cd\""),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void skipsJumps() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "print(\"a\")\n" +
                        "if x\n" +
                        "  print(x)\n" +
                        "end\n" +
                        "print(\"b\")"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(PRINT, "\"a\""),
                        new LogicInstruction(JUMP, var(1000), "equal", "x", "false"),
                        new LogicInstruction(PRINT, "x"),
                        new LogicInstruction(SET, var(1), "x"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(1), "null"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(PRINT, "\"b\""),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void mergesAcrossInstructions() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "print(\"Rate: \", rate, \" items/sec\", \"\\n\")\n" +
                        "print(\"Elapsed: \", @time - start, \" ms\")"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(PRINT, "\"Rate: \""),
                        new LogicInstruction(PRINT, "rate"),
                        new LogicInstruction(OP, "sub", var(0), "@time", "start"),
                        new LogicInstruction(PRINT, "\" items/sec\\nElapsed: \""),
                        new LogicInstruction(PRINT, var(0)),
                        new LogicInstruction(PRINT, "\" ms\""),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

}