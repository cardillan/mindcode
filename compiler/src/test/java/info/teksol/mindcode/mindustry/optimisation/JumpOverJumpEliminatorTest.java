package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.mindustry.generator.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

public class JumpOverJumpEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = new JumpOverJumpEliminator(terminus);

    @Test
    void optimizesBreakInWhileLoop() {
        final LogicInstructionPipeline pipeline = Optimisation.createPipelineOf(terminus,
            Optimisation.DEAD_CODE_ELIMINATION,
            Optimisation.SINGLE_STEP_JUMP_ELIMINATION,
            Optimisation.INPUT_TEMPS_ELIMINATION,
            Optimisation.JUMP_OVER_JUMP_ELIMINATION
        );

        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst(
                        "" +
                                "while true\n" +
                                "  print(\"In loop\")\n" +
                                "  if @unit.dead === 0\n" +
                                "    break\n" +
                                "  end\n" +
                                "end\n" +
                                "print(\"Out of loop\")"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(JUMP, var(1001), "equal", "true", "false"),
                        new LogicInstruction(PRINT, "\"In loop\""),
                        new LogicInstruction(SENSOR, var(0), "@unit", "@dead"),
                        new LogicInstruction(JUMP, var(1001), "strictEqual", var(0), "0"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(LABEL, var(1010)),
                        new LogicInstruction(JUMP, var(1000), "always"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(PRINT, "\"Out of loop\""),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesMinimalSequence() {
        List.of(
                new LogicInstruction(LABEL, "label0"),
                new LogicInstruction(JUMP, "label1", "equal", "a", "b"),
                new LogicInstruction(JUMP, "label0", "always"),
                new LogicInstruction(LABEL, "label1"),
                new LogicInstruction(END)
        ).forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                new LogicInstruction(LABEL, "label0"),
                new LogicInstruction(JUMP, "label0", "notEqual", "a", "b"),
                new LogicInstruction(LABEL, "label1"),
                new LogicInstruction(END)
                ),
                terminus.getResult()
        );
   }

    @Test
    void ignoresStrictEqual() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction(LABEL, "label0"),
                new LogicInstruction(JUMP, "label1", "strictEqual", "a", "b"),
                new LogicInstruction(JUMP, "label0", "always"),
                new LogicInstruction(LABEL, "label1"),
                new LogicInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresDistantJumps() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction(LABEL, "label0"),
                new LogicInstruction(JUMP, "label1", "strictEqual", "a", "b"),
                new LogicInstruction(JUMP, "label0", "always"),
                new LogicInstruction(PRINT, "a"),
                new LogicInstruction(LABEL, "label1"),
                new LogicInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresConditionalJumps() {
        List<LogicInstruction> sequence = List.of(
                new LogicInstruction(LABEL, "label0"),
                new LogicInstruction(JUMP, "label1", "equal", "a", "b"),
                new LogicInstruction(JUMP, "label0", "equal", "c", "d"),
                new LogicInstruction(LABEL, "label1"),
                new LogicInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

}