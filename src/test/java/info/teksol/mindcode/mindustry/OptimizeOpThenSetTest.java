package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

class OptimizeOpThenSetTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = new OptimizeOpThenSet(terminus);

    @Test
    void optimizesOpThenSet() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "r = rand(100)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "100"),
                        new LogicInstruction("op", "rand", "r", var(0)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesOpThenSetFromBinaryOp() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "remaining = capacity - current"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("op", "sub", "remaining", "capacity", "current"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }
}
