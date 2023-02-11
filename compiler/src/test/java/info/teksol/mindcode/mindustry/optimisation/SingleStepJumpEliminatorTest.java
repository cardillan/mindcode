package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

class SingleStepJumpEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = Optimisation.createPipelineOf(terminus, 
            Optimisation.CONDITIONAL_JUMPS_NORMALIZATION,
            Optimisation.DEAD_CODE_ELIMINATION,
            Optimisation.SINGLE_STEP_JUMP_ELIMINATION);

    @Test
    void removesSingleJump() {
        generateInto(
                sut,
                (Seq) translateToAst(
                        "if x 1 end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, var(1000), "equal", "x", "false"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void removesTwoJumps() {
        generateInto(
                sut,
                (Seq) translateToAst(
                        "if x if y 1 end end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, var(1000), "equal", "x", "false"),
                        createInstruction(JUMP, var(1002), "equal", "y", "false"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void keepsIsolatedJumps() {
        generateInto(
                sut,
                (Seq) translateToAst(
                        "if x print(a) else print(b) end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, var(1000), "equal", "x", "false"),
                        createInstruction(PRINT, "a"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(PRINT, "b"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
