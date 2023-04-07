package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

public class ConditionalJumpsNormalizerTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            Optimization.CONDITIONAL_JUMPS_NORMALIZATION);

    @Test
    void normalizesConditionalJump() {
        generateInto(
                sut,
                (Seq) translateToAst(""
                        + "while false\n"
                        + "  print(\"Here\")\n"
                        + "end\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(JUMP, var(1002), "always"),
                        createInstruction(PRINT, "\"Here\""),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void removesAlwaysFalseJump() {
        generateInto(
                sut,
                (Seq) translateToAst(
                        "while true 1 end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
