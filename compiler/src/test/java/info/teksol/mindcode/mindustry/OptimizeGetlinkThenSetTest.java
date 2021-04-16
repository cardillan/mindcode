package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

class OptimizeGetlinkThenSetTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = new OptimizeGetlinkThenSet(terminus);

    @Test
    void optimizesGetlinkFollowedBySet() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "target = getlink(0)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "0"),
                        new LogicInstruction("getlink", "target", var(0)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesGetlinkFollowedBySetForDifferentStuffAlone() {
        sut.emit(new LogicInstruction("getlink", "a", "0"));
        sut.emit(new LogicInstruction("set", "b", "1"));
        sut.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("getlink", "a", "0"),
                        new LogicInstruction("set", "b", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesGetlinkFollowedByGetlinkAlone() {
        sut.emit(new LogicInstruction("getlink", "a", "0"));
        sut.emit(new LogicInstruction("getlink", "b", "1"));
        sut.emit(new LogicInstruction("read", "c", "cell1", "1"));
        sut.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("getlink", "a", "0"),
                        new LogicInstruction("getlink", "b", "1"),
                        new LogicInstruction("read", "c", "cell1", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void terminalGetlinkIsEmitted() {
        sut.emit(new LogicInstruction("getlink", "a", "0"));
        sut.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("getlink", "a", "0")
                ),
                terminus.getResult()
        );
    }
}
