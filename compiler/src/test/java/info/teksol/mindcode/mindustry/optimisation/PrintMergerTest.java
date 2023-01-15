package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

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
                        new LogicInstruction("print", "\"ab\""),
                        new LogicInstruction("end")
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
                        new LogicInstruction("print", "\"abc\""),
                        new LogicInstruction("end")
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
                        new LogicInstruction("print", "\"a\""),
                        new LogicInstruction("print", "x"),
                        new LogicInstruction("print", "\"cd\""),
                        new LogicInstruction("end")
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
                        new LogicInstruction("print", "\"a\""),
                        new LogicInstruction("jump", var(1000), "notEqual", "x", "true"),
                        new LogicInstruction("print", "x"),
                        new LogicInstruction("set", var(1), "x"),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(1), "null"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("print", "\"b\""),
                        new LogicInstruction("end")
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
                        new LogicInstruction("print", "\"Rate: \""),
                        new LogicInstruction("print", "rate"),
                        new LogicInstruction("op", "sub", var(0), "@time", "start"),
                        new LogicInstruction("print", "\" items/sec\\nElapsed: \""),
                        new LogicInstruction("print", var(0)),
                        new LogicInstruction("print", "\" ms\""),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

}