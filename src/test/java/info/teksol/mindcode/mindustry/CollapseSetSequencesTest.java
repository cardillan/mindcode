package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

class CollapseSetSequencesTest extends AbstractGeneratorTest {
    private final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
    private final LogicInstructionPipeline pipeline = new CollapseSetSequences(terminus);

    @Test
    void collapsesMultiSetIntoASingleOne() {
        LogicInstructionGenerator.generateInto(
                pipeline, (Seq) translateToAst("n = 4")
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "n", "4"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void collapsesHeapWriteIntoConstantAccesses() {
        LogicInstructionGenerator.generateInto(
                pipeline, (Seq) translateToAst(
                        "cell1[2] = 4"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("write", "4", "cell1", "2"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void collapsesHeapReadIntoConstantAccesses() {
        LogicInstructionGenerator.generateInto(
                pipeline, (Seq) translateToAst(
                        "pop = cell1[2]"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("read", var(1), "cell1", "2"),
                        new LogicInstruction("set", "pop", var(1)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void collapsesSetToPrint() {
        LogicInstructionGenerator.generateInto(
                pipeline, (Seq) translateToAst(
                        "print(\"n: \", n, \"\\n\")"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("print", "\"n: \""),
                        new LogicInstruction("print", "n"),
                        new LogicInstruction("print", "\"\\n\""),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void collapsesSimpleAdd() {
        LogicInstructionGenerator.generateInto(
                pipeline, (Seq) translateToAst(
                        "n += 1"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("op", "add", var(1), "n", "1"),
                        new LogicInstruction("set", "n", var(1)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void collapsesMultipleSectionsInSequence() {
        LogicInstructionGenerator.generateInto(
                pipeline, (Seq) translateToAst(
                        "n = 4\n" +
                                "c = n + 2\n" +
                                "print(\"n: \", n)\n" +
                                "r = 2 * c\n" +
                                "print(\"\\n\")\n" +
                                "print(\"r: \", r)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "n", "4"),
                        new LogicInstruction("op", "add", var(1), "n", "2"),
                        new LogicInstruction("print", "\"n: \""),
                        new LogicInstruction("print", "n"),
                        new LogicInstruction("op", "mul", var(2), "2", var(1)),
                        new LogicInstruction("print", "\"\\n\""),
                        new LogicInstruction("print", "\"r: \""),
                        new LogicInstruction("print", var(2)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void doesNotChangeHowWhileLoopsFunction() {
        LogicInstructionGenerator.generateInto(
                pipeline, (Seq) translateToAst(
                        "RADIUS = 10\n" +
                                "\n" +
                                "while @unit === null\n" +
                                "  ubind(@poly)\n" +
                                "end\n" +
                                "\n" +
                                "approach(50, 100, RADIUS)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "RADIUS", "10"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("op", "strictEqual", var(1), "@unit", "null"),
                        new LogicInstruction("jump", var(1000), "notEqual", var(1), "true"),
                        new LogicInstruction("ubind", "@poly"),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("ucontrol", "approach", "50", "100", "RADIUS"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void collapsesAdjacentAssignments() {
        LogicInstructionGenerator.generateInto(
                pipeline, (Seq) translateToAst(
                        "result = 1 + 2"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("op", "add", var(1), "1", "2"),
                        new LogicInstruction("set", "result", var(1)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }
}
