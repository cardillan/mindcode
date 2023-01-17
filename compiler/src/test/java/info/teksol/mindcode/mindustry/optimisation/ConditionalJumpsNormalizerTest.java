package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.Opcode.*;

public class ConditionalJumpsNormalizerTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = Optimisation.createPipelineOf(terminus, 
            Optimisation.CONDITIONAL_JUMPS_NORMALIZATION);

    @Test
    void normalizesConditionalJump() {
        LogicInstructionGenerator.generateInto(
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
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(SET, var(1), "\"Here\""),
                        new LogicInstruction(PRINT, var(1)),
                        new LogicInstruction(SET, var(0), var(1)),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(0), "null"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void removesAlwaysFalseJump() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "if true 1 end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(1), "1"),
                        new LogicInstruction(SET, var(0), var(1)),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(0), "null"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
