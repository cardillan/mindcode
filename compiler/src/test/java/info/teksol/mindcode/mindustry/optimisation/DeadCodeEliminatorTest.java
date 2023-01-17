package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import org.junit.jupiter.api.Test;

import java.util.List;
import static info.teksol.mindcode.mindustry.Opcode.*;

class DeadCodeEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = Optimisation.createPipelineOf(terminus,
            Optimisation.DEAD_CODE_ELIMINATION);

    @Test
    void removesDeadSetsInIfExpression() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst("if x == 3\n  1\nelse\n  end()\nend")
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "3"),
                        new LogicInstruction(OP, "equal", var(1), "x", var(0)),
                        new LogicInstruction(JUMP, var(1000), "equal", var(1), "false"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(END),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void keepsUsefulIfAssignments() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst("n = if x == 3\n  1\nelse\n  41\nend\nmove(73, n)\n")
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "3"),
                        new LogicInstruction(OP, "equal", var(1), "x", var(0)),
                        new LogicInstruction(JUMP, var(1000), "equal", var(1), "false"),
                        new LogicInstruction(SET, var(3), "1"),
                        new LogicInstruction(SET, var(2), var(3)),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(4), "41"),
                        new LogicInstruction(SET, var(2), var(4)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(SET, "n", var(2)),
                        new LogicInstruction(SET, var(5), "73"),
                        new LogicInstruction(UCONTROL, "move", var(5), "n"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void preventsEliminationOfUradarUsages() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst("" +
                        "target = uradar(enemy, ground, any, health, MIN_TO_MAX, BY_DISTANCE)\n" +
                        "if target != null\n" +
                        "  approach(target.x, target.y, 10)\n" +
                        "  if within(target.x, target.y, 10)\n" +
                        "    target(target.x, target.y, SHOOT)\n" +
                        "  end\n" +
                        "end\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(URADAR, "enemy", "ground", "any", "health", "MIN_TO_MAX", "BY_DISTANCE", var(0)),
                        new LogicInstruction(SET, "target", var(0)),
                        new LogicInstruction(OP, "notEqual", var(1), "target", "null"),
                        new LogicInstruction(JUMP, var(1000), "equal", var(1), "false"),
                        new LogicInstruction(SENSOR, var(3), "target", "@x"),
                        new LogicInstruction(SENSOR, var(4), "target", "@y"),
                        new LogicInstruction(SET, var(5), "10"),
                        new LogicInstruction(UCONTROL, "approach", var(3), var(4), var(5)),
                        new LogicInstruction(SENSOR, var(6), "target", "@x"),
                        new LogicInstruction(SENSOR, var(7), "target", "@y"),
                        new LogicInstruction(SET, var(8), "10"),
                        new LogicInstruction(UCONTROL, "within", var(6), var(7), var(8), var(9)),
                        new LogicInstruction(JUMP, var(1002), "equal", var(9), "false"),
                        new LogicInstruction(SENSOR, var(11), "target", "@x"),
                        new LogicInstruction(SENSOR, var(12), "target", "@y"),
                        new LogicInstruction(UCONTROL, "target", var(11), var(12), "SHOOT"),
                        new LogicInstruction(JUMP, var(1003), "always"),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void preventsEliminationOfUlocateUsages() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst("" +
                        "ulocate(ore, @surge-alloy, outx, outy)\n" +
                        "approach(outx, outy, 4)\n" +
                        "ulocate(building, core, ENEMY, outx, outy, outbuilding)\n" +
                        "approach(outx, outy, 4)\n" +
                        "ulocate(spawn, outx, outy, outbuilding)\n" +
                        "approach(outx, outy, 4)\n" +
                        "ulocate(damaged, outx, outy, outbuilding)\n" +
                        "approach(outx, outy, 4)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", "outx", "outy", var(0), var(1)),
                        new LogicInstruction(SET, var(5), "4"),
                        new LogicInstruction(UCONTROL, "approach", "outx", "outy", var(5)),
                        new LogicInstruction(ULOCATE, "building", "core", "ENEMY", "@copper", "outx", "outy", var(2), "outbuilding"),
                        new LogicInstruction(SET, var(6), "4"),
                        new LogicInstruction(UCONTROL, "approach", "outx", "outy", var(6)),
                        new LogicInstruction(ULOCATE, "spawn", "core", "true", "@copper", "outx", "outy", var(3), "outbuilding"),
                        new LogicInstruction(SET, var(7), "4"),
                        new LogicInstruction(UCONTROL, "approach", "outx", "outy", var(7)),
                        new LogicInstruction(ULOCATE, "damaged", "core", "true", "@copper", "outx", "outy", var(4), "outbuilding"),
                        new LogicInstruction(SET, var(8), "4"),
                        new LogicInstruction(UCONTROL, "approach", "outx", "outy", var(8)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void completelyRemovesDeadcode() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst("" +
                        "n = 1\nn = 1\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
