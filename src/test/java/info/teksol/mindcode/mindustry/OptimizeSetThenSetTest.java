package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

class OptimizeSetThenSetTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = new OptimizeSetThenSet(terminus);

    @Test
    void consecutiveSetsThatRelateToEachOtherCollapse() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "a = 1\nb = a\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "a", "1"),
                        new LogicInstruction("set", "b", "a"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void consecutiveSetsThatDoNotRelateAreLeftAlone() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "a = 1\nb = 2\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "a", "1"),
                        new LogicInstruction("set", "b", "2"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void consecutiveSetsWithNoRelationshipAreLeftAlone() {
        sut.emit(new LogicInstruction("set", "x", "10"));
        sut.emit(new LogicInstruction("set", "y", "4"));
        sut.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "x", "10"),
                        new LogicInstruction("set", "y", "4")
                ),
                terminus.getResult()
        );
    }
}
