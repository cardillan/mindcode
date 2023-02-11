package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

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
        generateInto(
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
                        createInstruction(LABEL, "__start__"),
                        createInstruction(JUMP, "__start__", "equal", "a", "false"),
                        createInstruction(JUMP, var(1002), "equal", "b", "false"),
                        createInstruction(PRINT, "b"),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(PRINT, "a"),
                        createInstruction(JUMP, "__start__", "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void propagatesThroughConditionalTargets() {
        generateInto(
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
                        createInstruction(LABEL, var(1000)),
                        createInstruction(JUMP, var(1001), "notEqual", "c", "null"),
                        createInstruction(GETLINK, "c", "1"),
                        createInstruction(JUMP, var(1001), "notEqual", "c", "null"),
                        createInstruction(PRINT, "\"Not found\""),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINT, "\"Done\""),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void ignoresVolatileVariables() {
        generateInto(
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
                        createInstruction(LABEL, var(1000)),
                        createInstruction(JUMP, var(1001), "greaterThanEq", "@time", "wait"),
                        createInstruction(OP, "add", "n", "n", "1"),
                        createInstruction(JUMP, var(1000), "greaterThanEq", "@time", "wait"),
                        createInstruction(PRINT, "\"Waiting\""),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINT, "\"Done\""),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
