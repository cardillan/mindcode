package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import org.junit.jupiter.api.Test;

import java.util.List;

class ImproveConditionalJumpsTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = Optimisation.createPipelineOf(terminus,
            Optimisation.CONDITIONAL_JUMPS_IMPROVEMENT);
    
    @Test
    void collapsesUnnecessaryConditionals() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst(
                        "" +
                                "value = if cell1[4] == 0\n" +
                                "  false\n" +
                                "else\n" +
                                "  cell1[4] = true\n" +
                                "  n += 1\n" +
                                "end\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "4"),
                        new LogicInstruction("read", var(1), "cell1", var(0)),
                        new LogicInstruction("set", var(2), "0"),
                        new LogicInstruction("jump", var(1000), "notEqual", var(1), var(2)),
                        new LogicInstruction("set", var(4), "false"),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(5), "4"),
                        new LogicInstruction("write", "true", "cell1", var(5)),
                        new LogicInstruction("set", var(6), "1"),
                        new LogicInstruction("op", "add", var(7), "n", var(6)),
                        new LogicInstruction("set", "n", var(7)),
                        new LogicInstruction("set", var(4), var(7)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("set", "value", var(4)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void collapsesJumps() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst(
                        "" +
                                "while n > 0\n" +
                                "  n += 1\n" +
                                "end\n" +
                                "\n" +
                                "while n == null\n" +
                                "  n += 1\n" +
                                "end\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(0), "0"),
                        new LogicInstruction("jump", var(1001), "lessThanEq", "n", var(0)),
                        new LogicInstruction("set", var(2), "1"),
                        new LogicInstruction("op", "add", var(3), "n", var(2)),
                        new LogicInstruction("set", "n", var(3)),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),

                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("jump", var(1003), "notEqual", "n", "null"),
                        new LogicInstruction("set", var(5), "1"),
                        new LogicInstruction("op", "add", var(6), "n", var(5)),
                        new LogicInstruction("set", "n", var(6)),
                        new LogicInstruction("jump", var(1002), "always"),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void preservesUserVariables() {
        final LogicInstructionPipeline customPipeline = Optimisation.createPipelineOf(terminus,
                Optimisation.DEAD_CODE_ELIMINATION,
                Optimisation.OUTPUT_TEMPS_ELIMINATION,
                Optimisation.CONDITIONAL_JUMPS_IMPROVEMENT
        );

        LogicInstructionGenerator.generateInto(
                customPipeline,
                (Seq) translateToAst(
                        "alive = @unit.dead === 0\n" +
                                "if alive\n" +
                                "  print(alive)\n" +
                                "end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", var(0), "@unit", "@dead"),
                        new LogicInstruction("set", var(1), "0"),
                        new LogicInstruction("op", "strictEqual", "alive", var(0), var(1)),
                        new LogicInstruction("jump", var(1000), "equal", "alive", "false"),
                        new LogicInstruction("print", "alive"),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void preservesStrictEqualConditions() {
        final LogicInstructionPipeline customPipeline = Optimisation.createPipelineOf(terminus,
                Optimisation.DEAD_CODE_ELIMINATION,
                Optimisation.OUTPUT_TEMPS_ELIMINATION,
                Optimisation.CONDITIONAL_JUMPS_IMPROVEMENT
        );
    
        LogicInstructionGenerator.generateInto(
                customPipeline,
                (Seq) translateToAst(
                        "" +
                                "if @unit.dead === 0\n" +
                                "  print(alive)\n" +
                                "end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", var(0), "@unit", "@dead"),
                        new LogicInstruction("set", var(1), "0"),
                        new LogicInstruction("op", "strictEqual", var(2), var(0), var(1)),
                        new LogicInstruction("jump", var(1000), "equal", var(2), "false"),
                        new LogicInstruction("print", "alive"),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }
}