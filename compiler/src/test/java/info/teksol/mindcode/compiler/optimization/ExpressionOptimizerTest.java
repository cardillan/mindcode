package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class ExpressionOptimizerTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            Optimization.EXPRESSION_OPTIMIZATION);


    @Test
    void optimizesMulThenFloor1() {
        pipeline.emit(createInstruction(OP, mul, tmp0, value, K1000));
        pipeline.emit(createInstruction(OP, floor, result, tmp0));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, idiv, result, value, K0001),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesMulThenFloor2() {
        pipeline.emit(createInstruction(OP, mul, tmp0, K1000, value));
        pipeline.emit(createInstruction(OP, floor, result, tmp0));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, idiv, result, value, K0001),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesDivThenFloor() {
        pipeline.emit(createInstruction(OP, div, tmp0, value, K1000));
        pipeline.emit(createInstruction(OP, floor, result, tmp0));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, idiv, result, value, K1000),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesNonconstantDivThenFloor() {
        pipeline.emit(createInstruction(OP, div, tmp0, value, divisor));
        pipeline.emit(createInstruction(OP, floor, result, tmp0));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, idiv, result, value, divisor),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresNontemporaryVariables() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(OP, div, foo, value, K1000),
                createInstruction(OP, floor, result, foo),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresVariablesWithMultipleUsage() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(OP, div, tmp0, value, K1000),
                createInstruction(OP, floor, result, tmp0),
                createInstruction(SET, another, tmp0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresInstructionsInWrongOrder() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(OP, floor, result, tmp0),
                createInstruction(OP, div, tmp0, value, K1000),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresNonconstantMultiplicants() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(OP, mul, tmp0, value, divisor),
                createInstruction(OP, floor, result, tmp0),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }
}
