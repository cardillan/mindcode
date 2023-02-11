package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.generator.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

public class ConditionalJumpsNormalizerTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = Optimisation.createPipelineOf(terminus, 
            Optimisation.CONDITIONAL_JUMPS_NORMALIZATION);

    @Test
    void normalizesConditionalJump() {
        generateInto(
                sut,
                (Seq) translateToAst(
                        "" +
                                "if false\n" +
                                "  print(\"Here\")\n" +
                                "end\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(SET, var(1), "\"Here\""),
                        createInstruction(PRINT, var(1)),
                        createInstruction(SET, var(0), var(1)),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(0), "null"),
                        createInstruction(LABEL, var(1001)),
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
                        "if true 1 end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(1), "1"),
                        createInstruction(SET, var(0), var(1)),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(0), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
