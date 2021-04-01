package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

class OptimizeReadThenSetTest extends AbstractGeneratorTest {
    private final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
    private final LogicInstructionPipeline pipeline = new OptimizeReadThenSet(terminus);

    @Test
    void improvesReadThenSet() {
        LogicInstructionGenerator.generateInto(pipeline, (Seq) translateToAst("boo = cell1[0]"));

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "0"),
                        new LogicInstruction("read", "boo", "cell1", var(0)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesReadThenSetButOtherValueAlone() {
        pipeline.emit(new LogicInstruction("read", "tmp0", "cell1", "14"));
        pipeline.emit(new LogicInstruction("set", "tmp2", "14"));
        pipeline.emit(new LogicInstruction("end"));

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("read", "tmp0", "cell1", "14"),
                        new LogicInstruction("set", "tmp2", "14"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }
}
