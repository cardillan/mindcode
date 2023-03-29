package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.Optimisation;
import java.util.List;
import org.junit.jupiter.api.Test;

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
                        new LogicInstruction("jump", var(1001), "notEqual", "UNIT", "@poly"),
                        new LogicInstruction("set", var(0), "1"),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("jump", var(1002), "notEqual", "UNIT", "@mega"),
                        new LogicInstruction("set", var(0), "2"),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("set", var(0), "3"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", "x", var(0)),
                        new LogicInstruction("print", "x"),
                        new LogicInstruction("end")
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
                        new LogicInstruction("sensor", var(0), "vault1", "@firstItem"),
                        new LogicInstruction("jump", var(1001), "notEqual", var(0), "@lead"),
                        new LogicInstruction("set", var(1), "1"),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("jump", var(1002), "notEqual", var(0), "@coal"),
                        new LogicInstruction("set", var(1), "2"),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("set", var(1), "3"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", "x", var(1)),
                        new LogicInstruction("print", "x"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    
    @Test
    void optimizesMinimalSequence() {
        List.of(
                new LogicInstruction("sensor", "var", "vault1", "@firstItem"),
                new LogicInstruction("set", "__ast0", "var"),
                new LogicInstruction("jump", "label0", "notEqual", "__ast0", "@lead"),
                new LogicInstruction("jump", "label1", "notEqual", "__ast0", "@coal"),
                new LogicInstruction("end")
        ).forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "var", "vault1", "@firstItem"),
                        new LogicInstruction("jump", "label0", "notEqual", "var", "@lead"),
                        new LogicInstruction("jump", "label1", "notEqual", "var", "@coal"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresNonAstVariables() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction("sensor", "var", "vault1", "@firstItem"),
                new LogicInstruction("jump", "label0", "notEqual", "var", "@lead"),
                new LogicInstruction("jump", "label1", "notEqual", "var", "@coal"),
                new LogicInstruction("end")
        );
          
        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresNonAstJumps() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction("sensor", "var", "vault1", "@firstItem"),
                new LogicInstruction("set", "__ast0", "var"),
                new LogicInstruction("jump", "label0", "notEqual", "__ast0", "@lead"),
                new LogicInstruction("jump", "label1", "notEqual", "@coal", "__ast0"),
                new LogicInstruction("end")
        );
          
        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

    @Test
    void ignoresWrongOrder() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction("sensor", "var", "vault1", "@firstItem"),
                new LogicInstruction("jump", "label0", "notEqual", "__ast0", "@lead"),
                new LogicInstruction("set", "__ast0", "var"),
                new LogicInstruction("jump", "label1", "notEqual", "__ast0", "@coal"),
                new LogicInstruction("end")
        );
          
        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }

}
