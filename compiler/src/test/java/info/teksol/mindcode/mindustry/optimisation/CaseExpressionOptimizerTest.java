package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.Opcode.*;

public class CaseExpressionOptimizerTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = Optimisation.createPipelineOf(terminus,
            Optimisation.OUTPUT_TEMPS_ELIMINATION,
            Optimisation.CASE_EXPRESSION_OPTIMIZATION
    );
    
    @Test
    void optimizesCaseWithVariable() {
        LogicInstructionGenerator.generateInto(
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
                        new LogicInstruction(JUMP, var(1001), "notEqual", "UNIT", "@poly"),
                        new LogicInstruction(SET, var(0), "1"),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(JUMP, var(1002), "notEqual", "UNIT", "@mega"),
                        new LogicInstruction(SET, var(0), "2"),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(SET, var(0), "3"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, "x", var(0)),
                        new LogicInstruction(PRINT, "x"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesCaseWithExpression() {
        LogicInstructionGenerator.generateInto(
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
                        new LogicInstruction(SENSOR, var(0), "vault1", "@firstItem"),
                        new LogicInstruction(JUMP, var(1001), "notEqual", var(0), "@lead"),
                        new LogicInstruction(SET, var(1), "1"),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(JUMP, var(1002), "notEqual", var(0), "@coal"),
                        new LogicInstruction(SET, var(1), "2"),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(SET, var(1), "3"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, "x", var(1)),
                        new LogicInstruction(PRINT, "x"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    
    @Test
    void optimizesMinimalSequence() {
        List.of(
                new LogicInstruction(SENSOR, "var", "vault1", "@firstItem"),
                new LogicInstruction(SET, "__ast0", "var"),
                new LogicInstruction(JUMP, "label0", "notEqual", "__ast0", "@lead"),
                new LogicInstruction(JUMP, "label1", "notEqual", "__ast0", "@coal"),
                new LogicInstruction(END)
        ).forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, "var", "vault1", "@firstItem"),
                        new LogicInstruction(JUMP, "label0", "notEqual", "var", "@lead"),
                        new LogicInstruction(JUMP, "label1", "notEqual", "var", "@coal"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresNonAstVariables() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction(SENSOR, "var", "vault1", "@firstItem"),
                new LogicInstruction(JUMP, "label0", "notEqual", "var", "@lead"),
                new LogicInstruction(JUMP, "label1", "notEqual", "var", "@coal"),
                new LogicInstruction(END)
        );
          
        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresNonAstJumps() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction(SENSOR, "var", "vault1", "@firstItem"),
                new LogicInstruction(SET, "__ast0", "var"),
                new LogicInstruction(JUMP, "label0", "notEqual", "__ast0", "@lead"),
                new LogicInstruction(JUMP, "label1", "notEqual", "@coal", "__ast0"),
                new LogicInstruction(END)
        );
          
        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresWrongOrder() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction(SENSOR, "var", "vault1", "@firstItem"),
                new LogicInstruction(JUMP, "label0", "notEqual", "__ast0", "@lead"),
                new LogicInstruction(SET, "__ast0", "var"),
                new LogicInstruction(JUMP, "label1", "notEqual", "__ast0", "@coal"),
                new LogicInstruction(END)
        );
          
        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

}
