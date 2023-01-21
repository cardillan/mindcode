package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import org.junit.jupiter.api.Test;

import java.util.List;
import static info.teksol.mindcode.mindustry.Opcode.*;

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
                        new LogicInstruction(SET, var(0), "4"),
                        new LogicInstruction(READ, var(1), "cell1", var(0)),
                        new LogicInstruction(SET, var(2), "0"),
                        new LogicInstruction(JUMP, var(1000), "notEqual", var(1), var(2)),
                        new LogicInstruction(SET, var(4), "false"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(5), "4"),
                        new LogicInstruction(WRITE, "true", "cell1", var(5)),
                        new LogicInstruction(SET, var(6), "1"),
                        new LogicInstruction(OP, "add", var(7), "n", var(6)),
                        new LogicInstruction(SET, "n", var(7)),
                        new LogicInstruction(SET, var(4), var(7)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(SET, "value", var(4)),
                        new LogicInstruction(END)
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
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(0), "0"),
                        new LogicInstruction(JUMP, var(1001), "lessThanEq", "n", var(0)),
                        new LogicInstruction(SET, var(2), "1"),
                        new LogicInstruction(OP, "add", var(3), "n", var(2)),
                        new LogicInstruction(SET, "n", var(3)),
                        new LogicInstruction(LABEL, var(1010)),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),

                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(JUMP, var(1003), "notEqual", "n", "null"),
                        new LogicInstruction(SET, var(5), "1"),
                        new LogicInstruction(OP, "add", var(6), "n", var(5)),
                        new LogicInstruction(SET, "n", var(6)),
                        new LogicInstruction(LABEL, var(1011)),
                        new LogicInstruction(JUMP, var(1002), "always"),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(END)
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
                        new LogicInstruction(SENSOR, var(0), "@unit", "@dead"),
                        new LogicInstruction(SET, var(1), "0"),
                        new LogicInstruction(OP, "strictEqual", "alive", var(0), var(1)),
                        new LogicInstruction(JUMP, var(1000), "equal", "alive", "false"),
                        new LogicInstruction(PRINT, "alive"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
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
                        new LogicInstruction(SENSOR, var(0), "@unit", "@dead"),
                        new LogicInstruction(SET, var(1), "0"),
                        new LogicInstruction(OP, "strictEqual", var(2), var(0), var(1)),
                        new LogicInstruction(JUMP, var(1000), "equal", var(2), "false"),
                        new LogicInstruction(PRINT, "alive"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
}