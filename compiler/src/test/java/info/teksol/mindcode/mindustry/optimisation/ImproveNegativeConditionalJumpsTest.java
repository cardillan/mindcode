package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

class ImproveNegativeConditionalJumpsTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = Optimisation.createPipelineOf(terminus,
            Optimisation.CONDITIONAL_JUMPS_IMPROVEMENT);
    
    @Test
    void collapsesUnnecessaryConditionals() {
        generateInto(
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
                        createInstruction(SET, var(0), "4"),
                        createInstruction(READ, var(1), "cell1", var(0)),
                        createInstruction(SET, var(2), "0"),
                        createInstruction(JUMP, var(1000), "notEqual", var(1), var(2)),
                        createInstruction(SET, var(4), "false"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(5), "4"),
                        createInstruction(WRITE, "true", "cell1", var(5)),
                        createInstruction(SET, var(6), "1"),
                        createInstruction(OP, "add", var(7), "n", var(6)),
                        createInstruction(SET, "n", var(7)),
                        createInstruction(SET, var(4), var(7)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, "value", var(4)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void collapsesJumps() {
        generateInto(
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
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(0), "0"),
                        createInstruction(JUMP, var(1001), "lessThanEq", "n", var(0)),
                        createInstruction(SET, var(2), "1"),
                        createInstruction(OP, "add", var(3), "n", var(2)),
                        createInstruction(SET, "n", var(3)),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),

                        createInstruction(LABEL, var(1002)),
                        createInstruction(JUMP, var(1003), "notEqual", "n", "null"),
                        createInstruction(SET, var(5), "1"),
                        createInstruction(OP, "add", var(6), "n", var(5)),
                        createInstruction(SET, "n", var(6)),
                        createInstruction(LABEL, var(1011)),
                        createInstruction(JUMP, var(1002), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(END)
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

        generateInto(
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
                        createInstruction(SENSOR, var(0), "@unit", "@dead"),
                        createInstruction(SET, var(1), "0"),
                        createInstruction(OP, "strictEqual", "alive", var(0), var(1)),
                        createInstruction(JUMP, var(1000), "equal", "alive", "false"),
                        createInstruction(PRINT, "alive"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
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
    
        generateInto(
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
                        createInstruction(SENSOR, var(0), "@unit", "@dead"),
                        createInstruction(SET, var(1), "0"),
                        createInstruction(OP, "strictEqual", var(2), var(0), var(1)),
                        createInstruction(JUMP, var(1000), "equal", var(2), "false"),
                        createInstruction(PRINT, "alive"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
}