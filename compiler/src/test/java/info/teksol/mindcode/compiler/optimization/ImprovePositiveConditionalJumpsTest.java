package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class ImprovePositiveConditionalJumpsTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = new ImprovePositiveConditionalJumps(getInstructionProcessor(), terminus);
    
    @Test
    void optimizesMinimalSequence() {
        pipeline.emit(createInstruction(OP, "strictEqual", "__tmp0", "a", "b"));
        pipeline.emit(createInstruction(JUMP, "label0", "notEqual", "__tmp0", "false"));
        pipeline.emit(createInstruction(PRINT, "\"Not equal\""));
        pipeline.emit(createInstruction(LABEL, "label0"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, "label0", "strictEqual", "a", "b"),
                        createInstruction(PRINT, "\"Not equal\""),
                        createInstruction(LABEL, "label0"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
   }

    @Test
    void ignoresUserVariables() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(OP, "strictEqual", "c", "a", "b"),
                createInstruction(JUMP, "label0", "notEqual", "c", "false"),
                createInstruction(PRINT, "\"Not equal\""),
                createInstruction(LABEL, "label0"),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresDifferentVariables() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(OP, "strictEqual", "__tmp0", "a", "b"),
                createInstruction(JUMP, "label0", "notEqual", "__tmp1", "false"),
                createInstruction(PRINT, "\"Not equal\""),
                createInstruction(LABEL, "label0"),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresWrongConditions() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(OP, "strictEqual", "__tmp0", "a", "b"),
                createInstruction(JUMP, "label0", "equal", "__tmp0", "false"),
                createInstruction(PRINT, "\"Not equal\""),
                createInstruction(LABEL, "label0"),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresWrongOrder() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(JUMP, "label0", "notEqual", "__tmp0", "false"),
                createInstruction(OP, "strictEqual", "__tmp0", "a", "b"),
                createInstruction(PRINT, "\"Not equal\""),
                createInstruction(LABEL, "label0"),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }
}