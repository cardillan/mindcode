package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.Opcode.*;

class ImprovePositiveConditionalJumpsTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = new ImprovePositiveConditionalJumps(terminus);
    
    @Test
    void optimizesMinimalSequence() {
        List.of(
                new LogicInstruction(OP, "strictEqual", "__tmp0", "a", "b"),
                new LogicInstruction(JUMP, "label0", "notEqual", "__tmp0", "false"),
                new LogicInstruction(PRINT, "\"Not equal\""),
                new LogicInstruction(LABEL, "label0"),
                new LogicInstruction(END)
        ).forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                new LogicInstruction(JUMP, "label0", "strictEqual", "a", "b"),
                new LogicInstruction(PRINT, "\"Not equal\""),
                new LogicInstruction(LABEL, "label0"),
                new LogicInstruction(END)
                ),
                terminus.getResult()
        );
   }

    @Test
    void ignoresUserVariables() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction(OP, "strictEqual", "c", "a", "b"),
                new LogicInstruction(JUMP, "label0", "notEqual", "c", "false"),
                new LogicInstruction(PRINT, "\"Not equal\""),
                new LogicInstruction(LABEL, "label0"),
                new LogicInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresDifferentVariables() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction(OP, "strictEqual", "__tmp0", "a", "b"),
                new LogicInstruction(JUMP, "label0", "notEqual", "__tmp1", "false"),
                new LogicInstruction(PRINT, "\"Not equal\""),
                new LogicInstruction(LABEL, "label0"),
                new LogicInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresWrongConditions() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction(OP, "strictEqual", "__tmp0", "a", "b"),
                new LogicInstruction(JUMP, "label0", "equal", "__tmp0", "false"),
                new LogicInstruction(PRINT, "\"Not equal\""),
                new LogicInstruction(LABEL, "label0"),
                new LogicInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresWrongOrder() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction(JUMP, "label0", "notEqual", "__tmp0", "false"),
                new LogicInstruction(OP, "strictEqual", "__tmp0", "a", "b"),
                new LogicInstruction(PRINT, "\"Not equal\""),
                new LogicInstruction(LABEL, "label0"),
                new LogicInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }
}