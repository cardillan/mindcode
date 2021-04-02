package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogicInstructionPrinterTest extends AbstractAstTest {
    @Test
    void printsURadarAndUControl() {
        assertDoesNotThrow(() ->
                LogicInstructionPrinter.toString(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionGenerator.generateAndOptimize(
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
                                LogicInstructionGenerator.generateAndOptimize(
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
                                LogicInstructionGenerator.generateAndOptimize(
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
                                LogicInstructionGenerator.generateAndOptimize(
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

    @Test
    void correctlyDrawsTriangles() {
        assertEquals(
                "set __tmp0 20\n" +
                        "op sub __tmp1 x __tmp0\n" +
                        "set __tmp2 20\n" +
                        "op sub __tmp3 y __tmp2\n" +
                        "set __tmp4 20\n" +
                        "op add __tmp5 x __tmp4\n" +
                        "set __tmp6 20\n" +
                        "op sub __tmp7 y __tmp6\n" +
                        "set __tmp8 20\n" +
                        "op add __tmp9 x __tmp8\n" +
                        "set __tmp10 20\n" +
                        "op sub __tmp11 y __tmp10\n" +
                        "draw triangle __tmp1 __tmp3 __tmp5 __tmp7 __tmp9 __tmp11\n" +
                        "end\n",
                LogicInstructionPrinter.toString(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionGenerator.generateUnoptimized(
                                        (Seq) translateToAst(
                                                "triangle(x - 20, y - 20, x + 20, y - 20, x + 20, y - 20)"
                                        )
                                )
                        )
                )
        );
    }

    @Test
    void reallifeScripts() {
        assertEquals(
                "set STORAGE nucleus1\n" +
                        "set MSG message1\n" +
                        "sensor __tmp0 STORAGE @itemCapacity\n" +
                        "set capacity __tmp0\n" +
                        "set __tmp1 \"capacity: \"\n" +
                        "set __tmp2 \"\\n\"\n" +
                        "print __tmp1\n" +
                        "print capacity\n" +
                        "print __tmp2\n" +
                        "set __tmp3 0\n" +
                        "set n __tmp3\n" +
                        "op lessThan __tmp4 n @links\n" +
                        "jump 50 notEqual __tmp4 true\n" +
                        "getlink __tmp5 n\n" +
                        "set building __tmp5\n" +
                        "sensor __tmp6 building @type\n" +
                        "set type __tmp6\n" +
                        "op equal __tmp7 type @conveyor\n" +
                        "op equal __tmp8 type @titanium-conveyor\n" +
                        "op or __tmp9 __tmp7 __tmp8\n" +
                        "op equal __tmp10 type @plastanium-conveyor\n" +
                        "op or __tmp11 __tmp9 __tmp10\n" +
                        "jump 45 notEqual __tmp11 true\n" +
                        "sensor __tmp13 building @firstItem\n" +
                        "set resource __tmp13\n" +
                        "op notEqual __tmp14 resource null\n" +
                        "jump 42 notEqual __tmp14 true\n" +
                        "sensor __tmp16 nucleus1 @resource\n" +
                        "set level __tmp16\n" +
                        "op lessThan __tmp17 level capacity\n" +
                        "control enabled building __tmp17 0 0 0\n" +
                        "set __tmp18 \"\\n\"\n" +
                        "set __tmp19 \": \"\n" +
                        "set __tmp20 \" @ \"\n" +
                        "print __tmp18\n" +
                        "print n\n" +
                        "print __tmp19\n" +
                        "print resource\n" +
                        "print __tmp20\n" +
                        "print level\n" +
                        "set __tmp15 level\n" +
                        "jump 43 always 0 0\n" +
                        "set __tmp15 null\n" +
                        "set __tmp12 __tmp15\n" +
                        "jump 46 always 0 0\n" +
                        "set __tmp12 null\n" +
                        "set __tmp21 1\n" +
                        "op add __tmp22 n __tmp21\n" +
                        "set n __tmp22\n" +
                        "jump 11 always 0 0\n" +
                        "printflush MSG\n" +
                        "end\n",
                LogicInstructionPrinter.toString(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionGenerator.generateUnoptimized(
                                        (Seq) translateToAst(
                                                "STORAGE = nucleus1\n" +
                                                        "MSG = message1\n" +
                                                        "capacity = STORAGE.itemCapacity\n" +
                                                        "\n" +
                                                        "print(\"capacity: \", capacity, \"\\n\")\n" +
                                                        "\n" +
                                                        "for n = 0 ; n < @links ; n += 1\n" +
                                                        "  building = getlink(n)\n" +
                                                        "  type = building.type\n" +
                                                        "  if type == @conveyor\n" +
                                                        "     || type == @titanium-conveyor\n" +
                                                        "     || type == @plastanium-conveyor\n" +
                                                        "    resource = building.firstItem\n" +
                                                        "    if resource != null\n" +
                                                        "      level = nucleus1.resource\n" +
                                                        "      building.enabled = level < capacity\n" +
                                                        "      print(\"\\n\", n, \": \", resource, \" @ \", level)\n" +
                                                        "    end\n" +
                                                        "  end\n" +
                                                        "end\n" +
                                                        "printflush(MSG)\n"
                                        )
                                )
                        )
                )
        );
    }
}
