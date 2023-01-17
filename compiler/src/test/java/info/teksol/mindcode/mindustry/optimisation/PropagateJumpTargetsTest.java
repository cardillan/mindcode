package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.Opcode.*;

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
                        new LogicInstruction(LABEL, "__start__"),
                        new LogicInstruction(JUMP, "__start__", "equal", "a", "false"),
                        new LogicInstruction(JUMP, var(1002), "equal", "b", "false"),
                        new LogicInstruction(PRINT, "b"),
                        new LogicInstruction(JUMP, var(1003), "always"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(PRINT, "a"),
                        new LogicInstruction(JUMP, "__start__", "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
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
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(JUMP, var(1001), "notEqual", "c", "null"),
                        new LogicInstruction(GETLINK, "c", "1"),
                        new LogicInstruction(JUMP, var(1001), "notEqual", "c", "null"),
                        new LogicInstruction(PRINT, "\"Not found\""),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(PRINT, "\"Done\""),
                        new LogicInstruction(END)
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
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(JUMP, var(1001), "greaterThanEq", "@time", "wait"),
                        new LogicInstruction(OP, "add", "n", "n", "1"),
                        new LogicInstruction(JUMP, var(1000), "greaterThanEq", "@time", "wait"),
                        new LogicInstruction(PRINT, "\"Waiting\""),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(PRINT, "\"Done\""),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
