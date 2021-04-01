package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

class OptimizeSetThenReadTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = new OptimizeSetThenRead(terminus);

    @Test
    void correctlyOptimizesReadAtFixedAddress() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst("" +
                        "foo = cell1[14]"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("read", var(0), "cell1", "14"),
                        new LogicInstruction("set", "foo", var(0)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyOptimizesHeapAccess() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst("" +
                        "allocate heap in cell2[0 ... 4]\ntargetx = @thisx + $offsetx\ntargety = @thisy + $offsety\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("read", var(0), "cell2", "0"),
                        new LogicInstruction("op", "add", var(1), "@thisx", var(0)),
                        new LogicInstruction("set", "targetx", var(1)),
                        new LogicInstruction("read", var(2), "cell2", "1"),
                        new LogicInstruction("op", "add", var(3), "@thisy", var(2)),
                        new LogicInstruction("set", "targety", var(3)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void keepsTerminalSet() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst("" +
                        "bar = foo"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "bar", "foo"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void keepsTerminalSet1() {
        sut.emit(new LogicInstruction("set", "x", "1"));
        sut.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "x", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyHandlesSetThenReadWithDifferingValues() {
        sut.emit(new LogicInstruction("set", "x", "1"));
        sut.emit(new LogicInstruction("read", "tmp0", "cell2", "14"));
        sut.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "x", "1"),
                        new LogicInstruction("read", var(0), "cell2", "14")
                ),
                terminus.getResult()
        );
    }
}
