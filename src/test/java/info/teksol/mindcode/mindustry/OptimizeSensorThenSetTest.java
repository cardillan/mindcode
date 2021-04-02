package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

class OptimizeSensorThenSetTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = new OptimizeSensorThenSet(terminus);

    @Test
    void optimizesSensorThenSet() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst(
                        "numsilicon = STORAGE.silicon"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "numsilicon", "STORAGE", "@silicon"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void supportsConsecutiveSensors() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst(
                        "numgraphite = container1.graphite\nnumcoal = container1.coal"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "numgraphite", "container1", "@graphite"),
                        new LogicInstruction("sensor", "numcoal", "container1", "@coal"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSensorThenSetWithUnrelatedAlone() {
        sut.emit(new LogicInstruction("sensor", "numsil", "vault1", "@silicon"));
        sut.emit(new LogicInstruction("set", "n", "1"));
        sut.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "numsil", "vault1", "@silicon"),
                        new LogicInstruction("set", "n", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSensorNonSetAlone() {
        sut.emit(new LogicInstruction("sensor", "numsil", "vault1", "@silicon"));
        sut.emit(new LogicInstruction("print", "numsil"));
        sut.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "numsil", "vault1", "@silicon"),
                        new LogicInstruction("print", "numsil")
                ),
                terminus.getResult()
        );
    }

    @Test
    void consecutiveSensorsOptimizeCorrectly() {
        sut.emit(new LogicInstruction("sensor", "a", "vault1", "@silicon"));
        sut.emit(new LogicInstruction("sensor", "b", "conveyor1", "@enabled"));
        sut.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "a", "vault1", "@silicon"),
                        new LogicInstruction("sensor", "b", "conveyor1", "@enabled")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSingleSensorAlone() {
        sut.emit(new LogicInstruction("sensor", "numsil", "vault1", "@silicon"));
        sut.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "numsil", "vault1", "@silicon")
                ),
                terminus.getResult()
        );
    }
}
