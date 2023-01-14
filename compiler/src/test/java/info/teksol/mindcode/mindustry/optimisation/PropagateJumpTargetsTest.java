package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PropagateJumpTargetsTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = Optimisation.createPipelineOf(terminus, 
            Optimisation.DEAD_CODE_ELIMINATION,
            Optimisation.CONDITIONAL_JUMPS_IMPROVEMENT,
            Optimisation.JUMP_TARGET_PROPAGATION,
            Optimisation.INPUT_TEMPS_ELIMINATION,
            Optimisation.OUTPUT_TEMPS_ELIMINATION
    );

    @Test
    void propagatesThroughUnconditionalTargets() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "" +
                                "if a\n" +
                                "  if b\n" +
                                "    print(b)\n" +
                                "  end\n" +
                                "  print(a)\n" +
                                "end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("label", "__start__"),
                        new LogicInstruction("jump", "__start__", "notEqual", "a", "true"),
                        new LogicInstruction("jump", var(1002), "notEqual", "b", "true"),
                        new LogicInstruction("print", "b"),
                        new LogicInstruction("jump", var(1003), "always"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("print", "a"),
                        new LogicInstruction("jump", "__start__", "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void propagatesThroughConditionalTargets() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "" +
                                "while c == null\n" +
                                "  c = getlink(1)\n" +
                                "  if c == null\n" +
                                "    print(\"Not found\")\n" +
                                "  end\n" +
                                "end\n" +
                                "print(\"Done\")"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("jump", var(1001), "notEqual", "c", "null"),
                        new LogicInstruction("getlink", "c", "1"),
                        new LogicInstruction("jump", var(1001), "notEqual", "c", "null"),
                        new LogicInstruction("print", "\"Not found\""),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("print", "\"Done\""),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void ignoresVolatileVariables() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "" +
                                "while @time < wait\n" +
                                "  n += 1\n" +
                                "  if @time < wait\n" +
                                "    print(\"Waiting\")\n" +
                                "  end\n" +
                                "end\n" +
                                "print(\"Done\")"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("jump", var(1001), "greaterThanEq", "@time", "wait"),
                        new LogicInstruction("op", "add", "n", "n", "1"),
                        new LogicInstruction("jump", var(1000), "greaterThanEq", "@time", "wait"),
                        new LogicInstruction("print", "\"Waiting\""),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("jump", var(1000), "always"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("print", "\"Done\""),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }
}
