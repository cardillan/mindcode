package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicBoolean;
import info.teksol.mindcode.logic.Operation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class ImprovePositiveConditionalJumpsTest extends AbstractOptimizerTest<ImprovePositiveConditionalJumps> {

    @Override
    protected Class<ImprovePositiveConditionalJumps> getTestedClass() {
        return ImprovePositiveConditionalJumps.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(Optimization.JUMP_OVER_JUMP_ELIMINATION);
    }

    @Override
    protected MindcodeOptimizer createMindcodeOptimizer(TestCompiler compiler) {
        return new MindcodeOptimizer(compiler.processor, compiler.profile, compiler.messages::add) {
            @Override
            protected List<Optimizer> getOptimizers() {
                return List.of(new ImprovePositiveConditionalJumps(compiler.processor));
            }
        };
    }

    @Test
    void optimizesMinimalSequence() {
        assertOptimizesTo(
                List.of(
                        createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                        createInstruction(JUMP, label0, Condition.NOT_EQUAL, tmp0, LogicBoolean.FALSE),
                        createInstruction(PRINT, message),
                        createInstruction(LABEL, label0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(JUMP, label0, Operation.STRICT_EQUAL, a, b),
                        createInstruction(PRINT, message),
                        createInstruction(LABEL, label0),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresUserVariables() {
        assertDoesNotOptimize(
                createInstruction(OP, Operation.STRICT_EQUAL, c, a, b),
                createInstruction(JUMP, label0, Condition.NOT_EQUAL, c, LogicBoolean.FALSE),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresDifferentVariables() {
        assertDoesNotOptimize(
                createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                createInstruction(JUMP, label0, Condition.NOT_EQUAL, tmp1, LogicBoolean.FALSE),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresWrongConditions() {
        assertDoesNotOptimize(
                createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                createInstruction(JUMP, label0, Condition.EQUAL, tmp0, LogicBoolean.FALSE),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresWrongOrder() {
        assertDoesNotOptimize(
                createInstruction(JUMP, label0, Condition.NOT_EQUAL, tmp0, LogicBoolean.FALSE),
                createInstruction(OP, Operation.STRICT_EQUAL, tmp0, a, b),
                createInstruction(PRINT, message),
                createInstruction(LABEL, label0),
                createInstruction(END)
        );
    }
}