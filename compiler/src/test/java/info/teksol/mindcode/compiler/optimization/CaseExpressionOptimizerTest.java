package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class CaseExpressionOptimizerTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            Optimization.OUTPUT_TEMPS_ELIMINATION,
            Optimization.JUMP_OVER_JUMP_ELIMINATION,
            Optimization.CASE_EXPRESSION_OPTIMIZATION
    );
    
    @Test
    void optimizesCaseWithVariable() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "x = case UNIT\n" +
                        "  when @poly 1\n" +
                        "  when @mega 2\n" +
                        "  else 3\n" +
                        "end\n" +
                        "print(x)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, var(1001), "notEqual", "UNIT", "@poly"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(0), "1"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1003), "notEqual", "UNIT", "@mega"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(SET, var(0), "2"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(0), "3"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, "x", var(0)),
                        createInstruction(PRINT, "x"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesCaseWithExpression() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "x = case vault1.firstItem\n" +
                        "  when @lead 1\n" +
                        "  when @coal 2\n" +
                        "  else 3\n" +
                        "end\n" +
                        "print(x)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var(0), "vault1", "@firstItem"),
                        createInstruction(JUMP, var(1001), "notEqual", var(0), "@lead"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(1), "1"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1003), "notEqual", var(0), "@coal"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(SET, var(1), "2"),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(1), "3"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, "x", var(1)),
                        createInstruction(PRINT, "x"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    
    @Test
    void optimizesMinimalSequence() {
        List.of(
                createInstruction(SENSOR, "var", "vault1", "@firstItem"),
                createInstruction(SET, "__ast0", "var"),
                createInstruction(JUMP, "label0", "equal", "__ast0", "@lead"),
                createInstruction(JUMP, "label1", "equal", "__ast0", "@coal"),
                createInstruction(END)
        ).forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "var", "vault1", "@firstItem"),
                        createInstruction(JUMP, "label0", "equal", "var", "@lead"),
                        createInstruction(JUMP, "label1", "equal", "var", "@coal"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresNonAstVariables() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SENSOR, "var", "vault1", "@firstItem"),
                createInstruction(JUMP, "label0", "notEqual", "var", "@lead"),
                createInstruction(JUMP, "label1", "notEqual", "var", "@coal"),
                createInstruction(END)
        );
          
        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresNonAstJumps() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SENSOR, "var", "vault1", "@firstItem"),
                createInstruction(SET, "__ast0", "var"),
                createInstruction(JUMP, "label0", "notEqual", "__ast0", "@lead"),
                createInstruction(JUMP, "label1", "notEqual", "@coal", "__ast0"),
                createInstruction(END)
        );
          
        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresWrongOrder() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(SENSOR, "var", "vault1", "@firstItem"),
                createInstruction(JUMP, "label0", "notEqual", "__ast0", "@lead"),
                createInstruction(SET, "__ast0", "var"),
                createInstruction(JUMP, "label1", "notEqual", "__ast0", "@coal"),
                createInstruction(END)
        );
          
        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

}
