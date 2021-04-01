package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

class OptimizeSetThenOpTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = new OptimizeSetThenOp(terminus);

    @Test
    void optimizesSetThenOp() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst(
                        "FLAG = floor(rand(10000))\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("op", "rand", var(0), "10000"),
                        new LogicInstruction("op", "floor", var(1), var(0)),
                        new LogicInstruction("set", "FLAG", var(1)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSetThenOpWhenOpDoesNotUseSetAlone() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst(
                        "r = 2\np = rand(500)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "2"),
                        new LogicInstruction("set", "r", var(0)),
                        new LogicInstruction("op", "rand", var(1), "500"),
                        new LogicInstruction("set", "p", var(1)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizeSetThenOpWithBinaryOpFirst() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst(
                        "x = 41\npos = 70 + x"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "41"),
                        new LogicInstruction("set", "x", var(0)),
                        new LogicInstruction("op", "add", var(2), "70", "x"),
                        new LogicInstruction("set", "pos", var(2)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizeSetThenOpWithBinaryOpLast() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst(
                        "x = 41\npos = x + 70"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "41"),
                        new LogicInstruction("set", "x", var(0)),
                        new LogicInstruction("op", "add", var(2), "x", "70"),
                        new LogicInstruction("set", "pos", var(2)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizeSetThenOpWithBinaryOpBoth() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst(
                        "x = 41\ny = 72\npos = x + y"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "41"),
                        new LogicInstruction("set", "x", var(0)),
                        new LogicInstruction("set", var(1), "72"),
                        new LogicInstruction("set", "y", var(1)),
                        new LogicInstruction("op", "add", var(2), "x", "y"),
                        new LogicInstruction("set", "pos", var(2)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }
}
