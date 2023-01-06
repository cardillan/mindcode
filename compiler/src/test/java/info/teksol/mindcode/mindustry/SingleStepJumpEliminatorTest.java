package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

class SingleStepJumpEliminatorTest extends AbstractGeneratorTest {
    // Sequences of jumps are not generated without dead code elimination
    private final LogicInstructionPipeline sut = new DeadCodeEliminator(new SingleStepJumpEliminator(terminus));

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
                        new LogicInstruction("jump", var(1000), "notEqual", "x", "true"),
                        new LogicInstruction("jump", var(1002), "notEqual", "y", "true"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("jump", var(1000), "notEqual", "x", "true"),
                        new LogicInstruction("jump", var(1002), "notEqual", "y", "true"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }
}
