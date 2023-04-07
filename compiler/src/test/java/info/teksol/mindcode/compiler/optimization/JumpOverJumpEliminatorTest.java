package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

public class JumpOverJumpEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = new JumpOverJumpEliminator(getInstructionProcessor(), terminus);

    @Test
    void optimizesBreakInWhileLoop() {
        final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
                terminus,
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.SINGLE_STEP_JUMP_ELIMINATION,
                Optimization.INPUT_TEMPS_ELIMINATION,
                Optimization.JUMP_OVER_JUMP_ELIMINATION
        );

        generateInto(
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
                        createInstruction(LABEL, var(1000)),
                        createInstruction(JUMP, var(1001), "equal", "true", "false"),
                        createInstruction(PRINT, "\"In loop\""),
                        createInstruction(SENSOR, var(0), "@unit", "@dead"),
                        createInstruction(JUMP, var(1001), "strictEqual", var(0), "0"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(JUMP, var(1000), "always"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINT, "\"Out of loop\""),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesMinimalSequence() {
        List.of(
                createInstruction(LABEL, "label0"),
                createInstruction(JUMP, "label1", "equal", "a", "b"),
                createInstruction(JUMP, "label0", "always"),
                createInstruction(LABEL, "label1"),
                createInstruction(END)
        ).forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                createInstruction(LABEL, "label0"),
                createInstruction(JUMP, "label0", "notEqual", "a", "b"),
                createInstruction(LABEL, "label1"),
                createInstruction(END)
                ),
                terminus.getResult()
        );
   }

    @Test
    void ignoresStrictEqual() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(LABEL, "label0"),
                createInstruction(JUMP, "label1", "strictEqual", "a", "b"),
                createInstruction(JUMP, "label0", "always"),
                createInstruction(LABEL, "label1"),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresDistantJumps() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(LABEL, "label0"),
                createInstruction(JUMP, "label1", "strictEqual", "a", "b"),
                createInstruction(JUMP, "label0", "always"),
                createInstruction(PRINT, "a"),
                createInstruction(LABEL, "label1"),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

    @Test
    void ignoresConditionalJumps() {
        List<LogicInstruction> sequence = List.of(
                createInstruction(LABEL, "label0"),
                createInstruction(JUMP, "label1", "equal", "a", "b"),
                createInstruction(JUMP, "label0", "equal", "c", "d"),
                createInstruction(LABEL, "label1"),
                createInstruction(END)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
   }

}