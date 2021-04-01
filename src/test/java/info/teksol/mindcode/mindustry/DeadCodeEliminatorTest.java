package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

class DeadCodeEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline sut = new DeadCodeEliminator(terminus);

    @Test
    void removesDeadSetsInIfExpression() {
        LogicInstructionGenerator.generateInto(sut,
                (Seq) translateToAst("if x == 3\n  1\nelse\n  end()\nend")
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "3"),
                        new LogicInstruction("op", "equal", var(1), "x", var(0)),
                        new LogicInstruction("jump", var(1000), "notEqual", var(1), "true"),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("end"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "3"),
                        new LogicInstruction("op", "equal", var(1), "x", var(0)),
                        new LogicInstruction("jump", var(1000), "notEqual", var(1), "true"),
                        new LogicInstruction("set", var(3), "1"),
                        new LogicInstruction("set", var(2), var(3)),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(4), "41"),
                        new LogicInstruction("set", var(2), var(4)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("set", "n", var(2)),
                        new LogicInstruction("set", var(5), "73"),
                        new LogicInstruction("ucontrol", "move", var(5), "n"),
                        new LogicInstruction("end")
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
                        new LogicInstruction("uradar", "enemy", "ground", "any", "health", "MIN_TO_MAX", "BY_DISTANCE", var(0)),
                        new LogicInstruction("set", "target", var(0)),
                        new LogicInstruction("op", "notEqual", var(1), "target", "null"),
                        new LogicInstruction("jump", var(1000), "notEqual", var(1), "true"),
                        new LogicInstruction("sensor", var(3), "target", "@x"),
                        new LogicInstruction("sensor", var(4), "target", "@y"),
                        new LogicInstruction("set", var(5), "10"),
                        new LogicInstruction("ucontrol", "approach", var(3), var(4), var(5)),
                        new LogicInstruction("sensor", var(6), "target", "@x"),
                        new LogicInstruction("sensor", var(7), "target", "@y"),
                        new LogicInstruction("set", var(8), "10"),
                        new LogicInstruction("ucontrol", "within", var(6), var(7), var(8), var(9)),
                        new LogicInstruction("jump", var(1002), "notEqual", var(9), "true"),
                        new LogicInstruction("sensor", var(11), "target", "@x"),
                        new LogicInstruction("sensor", var(12), "target", "@y"),
                        new LogicInstruction("ucontrol", "target", var(11), var(12), "SHOOT"),
                        new LogicInstruction("jump", var(1003), "always"),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("jump", var(1001), "always"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("ulocate", "ore", "core", "true", "@surge-alloy", "outx", "outy", var(0), var(1)),
                        new LogicInstruction("set", var(5), "4"),
                        new LogicInstruction("ucontrol", "approach", "outx", "outy", var(5)),
                        new LogicInstruction("ulocate", "building", "core", "ENEMY", "@copper", "outx", "outy", var(2), "outbuilding"),
                        new LogicInstruction("set", var(6), "4"),
                        new LogicInstruction("ucontrol", "approach", "outx", "outy", var(6)),
                        new LogicInstruction("ulocate", "spawn", "core", "true", "@copper", "outx", "outy", var(3), "outbuilding"),
                        new LogicInstruction("set", var(7), "4"),
                        new LogicInstruction("ucontrol", "approach", "outx", "outy", var(7)),
                        new LogicInstruction("ulocate", "damaged", "core", "true", "@copper", "outx", "outy", var(4), "outbuilding"),
                        new LogicInstruction("set", var(8), "4"),
                        new LogicInstruction("ucontrol", "approach", "outx", "outy", var(8)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }
}
