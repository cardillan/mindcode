package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LogicInstructionPrinterTest extends AbstractAstTest {
    @Test
    void printsURadarAndUControl() {
        assertDoesNotThrow(() ->
                LogicInstructionPrinter.toString(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionGenerator.generateFrom(
                                        (Seq) translateToAst("" +
                                                "target = uradar(enemy, ground, any, health, MIN_TO_MAX, BY_DISTANCE)\n" +
                                                "if target != null\n" +
                                                "  approach(target.x, target.y, 10)\n" +
                                                "  if within(target.x, target.y, 10)\n" +
                                                "    target(target.x, target.y, SHOOT)\n" +
                                                "  end\n" +
                                                "end\n"
                                        )
                                )
                        )
                )
        );
    }

    @Test
    void printsULocate() {
        assertDoesNotThrow(() ->
                LogicInstructionPrinter.toString(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionGenerator.generateFrom(
                                        (Seq) translateToAst("" +
                                                "ulocate(ore, @surge-alloy, outx, outy)\n" +
                                                "ulocate(building, core, ENEMY, outx, outy, outbuilding)\n" +
                                                "ulocate(spawn, outx, outy, outbuilding)\n" +
                                                "ulocate(damaged, outx, outy, outbuilding)\n"
                                        )
                                )
                        )
                )
        );
    }


    @Test
    void realLifeScripts1() {
        assertThrows(InsufficientArgumentsException.class, () ->
                LogicInstructionPrinter.toString(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionGenerator.generateFrom(
                                        (Seq) translateToAst(
                                                "flag = 33548\n" +
                                                        "\n" +
                                                        "ubind(@poly)\n" +
                                                        "if @unit.flag != flag\n" +
                                                        "  end()\n" +
                                                        "end\n" +
                                                        "\n" +
                                                        "ulocate(building, core, false, found, building)\n" +
                                                        "\n" +
                                                        "if @unit.totalItems < @unit.itemCapacity\n" +
                                                        "  approach(container1.x, container1.y, 5)\n" +
                                                        "  itemTake(container1, @silicon, @unit.itemCapacity - @unit.totalItems)\n" +
                                                        "else\n" +
                                                        "  approach(found.x, found.y, 5)\n" +
                                                        "  itemDrop(found, @unit.totalItems)\n" +
                                                        "end\n"
                                        )
                                )
                        )
                )
        );
    }

    @Test
    void realLifeScripts2() {
        assertDoesNotThrow(() ->
                LogicInstructionPrinter.toString(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionGenerator.generateFrom(
                                        (Seq) translateToAst(
                                                "leader = getlink(0)\n" +
                                                        "count = 1\n" +
                                                        "while count < @links\n" +
                                                        "  turret = getlink(count)\n" +
                                                        "  turret.shoot(leader.shootX, leader.shootY, leader.shooting)\n" +
                                                        "  count = count + 1\n" +
                                                        "end"
                                        )
                                )
                        )
                )
        );
    }
}
