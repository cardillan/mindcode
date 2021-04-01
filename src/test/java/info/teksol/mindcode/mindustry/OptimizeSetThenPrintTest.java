package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

class OptimizeSetThenPrintTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = new OptimizeSetThenPrint(terminus);

    @Test
    void optimizesSetThenPrint() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "print(\"a: \", a)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("print", "\"a: \""),
                        new LogicInstruction("print", "a"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSetThenReadAlone() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "a = cell4[10]"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "10"),
                        new LogicInstruction("read", var(1), "cell4", var(0)),
                        new LogicInstruction("set", "a", var(1)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSetThenPrintUnrelatedUndisturbed() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "x = 1\nprint(y)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "1"),
                        new LogicInstruction("set", "x", var(0)),
                        new LogicInstruction("print", "y"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyFlushesWhenInAnExpectingPrintState() {
        sut.emit(new LogicInstruction("set", "foo", "bar"));
        sut.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "foo", "bar")
                ),
                terminus.getResult()
        );
    }
}
