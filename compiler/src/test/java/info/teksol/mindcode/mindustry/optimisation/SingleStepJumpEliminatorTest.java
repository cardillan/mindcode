package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import org.junit.jupiter.api.Test;

import java.util.List;
import static info.teksol.mindcode.mindustry.Opcode.*;

class SingleStepJumpEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = Optimisation.createPipelineOf(terminus, 
            Optimisation.CONDITIONAL_JUMPS_NORMALIZATION,
            Optimisation.DEAD_CODE_ELIMINATION,
            Optimisation.SINGLE_STEP_JUMP_ELIMINATION);

    @Test
    void removesSingleJump() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "if x 1 end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(JUMP, var(1000), "equal", "x", "false"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void removesTwoJumps() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "if x if y 1 end end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(JUMP, var(1000), "equal", "x", "false"),
                        new LogicInstruction(JUMP, var(1002), "equal", "y", "false"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void keepsIsolatedJumps() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "if x print(a) else print(b) end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(JUMP, var(1000), "equal", "x", "false"),
                        new LogicInstruction(PRINT, "a"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(PRINT, "b"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
