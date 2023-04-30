package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicBoolean;
import info.teksol.mindcode.logic.Operation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class ImprovePositiveConditionalJumpsTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = new ImprovePositiveConditionalJumps(getInstructionProcessor(), terminus);



    @Test
    void optimizesMinimalSequence() {
        pipeline.emit(createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b));
        pipeline.emit(createInstruction(JUMP, label0, Condition.NOT_EQUAL, tmp0, LogicBoolean.FALSE));
        pipeline.emit(createInstruction(PRINT, message));
        pipeline.emit(createInstruction(LABEL, label0));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, label0, Operation.STRICT_EQUAL, a, b),
                        createInstruction(PRINT, message),
                        createInstruction(LABEL, label0),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
   }

    @Test
    void ignoresUserVariables() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(OP, Operation.STRICT_EQUAL, c, a, b),
                createInstruction(JUMP, label0, Condition.NOT_EQUAL, c, LogicBoolean.FALSE),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresDifferentVariables() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                createInstruction(JUMP, label0, Condition.NOT_EQUAL, tmp1, LogicBoolean.FALSE),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresWrongConditions() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                createInstruction(JUMP, label0, Condition.EQUAL, tmp0, LogicBoolean.FALSE),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresWrongOrder() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(JUMP, label0, Condition.NOT_EQUAL, tmp0, LogicBoolean.FALSE),
                createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }
}