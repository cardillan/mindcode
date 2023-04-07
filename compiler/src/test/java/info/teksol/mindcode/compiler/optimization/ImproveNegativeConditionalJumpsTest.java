package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class ImproveNegativeConditionalJumpsTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            Optimization.CONDITIONAL_JUMPS_IMPROVEMENT);
    
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
                        createInstruction(READ, var(0), "cell1", "4"),
                        createInstruction(JUMP, var(1000), "notEqual", var(0), "0"),
                        createInstruction(SET, var(2), "false"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(WRITE, "true", "cell1", "4"),
                        createInstruction(OP, "add", var(3), "n", "1"),
                        createInstruction(SET, "n", var(3)),
                        createInstruction(SET, var(2), var(3)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, "value", var(2)),
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
                        createInstruction(JUMP, var(1002), "lessThanEq", "n", "0"),
                        createInstruction(OP, "add", var(1), "n", "1"),
                        createInstruction(SET, "n", var(1)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(JUMP, var(1005), "notEqual", "n", "null"),
                        createInstruction(OP, "add", var(3), "n", "1"),
                        createInstruction(SET, "n", var(3)),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1005)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void preservesUserVariables() {
        final LogicInstructionPipeline customPipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
                terminus,
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.OUTPUT_TEMPS_ELIMINATION,
                Optimization.CONDITIONAL_JUMPS_IMPROVEMENT
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
                        createInstruction(OP, "strictEqual", "alive", var(0), "0"),
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
        final LogicInstructionPipeline customPipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
                terminus,
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.OUTPUT_TEMPS_ELIMINATION,
                Optimization.CONDITIONAL_JUMPS_IMPROVEMENT
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
                        createInstruction(OP, "strictEqual", var(1), var(0), "0"),
                        createInstruction(JUMP, var(1000), "equal", var(1), "false"),
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