package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.functions.WrongNumberOfParametersException;
import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogicInstructionPrinterTest extends AbstractGeneratorTest {
    @Test
    void printsURadarAndUControl() {
        assertDoesNotThrow(() ->
                LogicInstructionPrinter.toString(getInstructionProcessor(),
                        LogicInstructionLabelResolver.resolve(
                                getInstructionProcessor(),
                                generateAndOptimize(
                                        (Seq) translateToAst("" +
                                                "target = uradar(enemy, ground, any, health, MIN_TO_MAX)\n" +
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
                LogicInstructionPrinter.toString(getInstructionProcessor(),
                        LogicInstructionLabelResolver.resolve(
                                getInstructionProcessor(),
                                generateAndOptimize(
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
        assertThrows(WrongNumberOfParametersException.class, () ->
                LogicInstructionPrinter.toString(getInstructionProcessor(),
                        LogicInstructionLabelResolver.resolve(
                                getInstructionProcessor(),
                                generateAndOptimize(
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
                                                        "  itemDrop(found, @silicon, @unit.totalItems)\n" +
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
                LogicInstructionPrinter.toString(getInstructionProcessor(),
                        LogicInstructionLabelResolver.resolve(
                                getInstructionProcessor(),
                                generateAndOptimize(
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
                "op sub __tmp0 x 20\n" +
                        "op sub __tmp1 y 20\n" +
                        "op add __tmp2 x 20\n" +
                        "op sub __tmp3 y 20\n" +
                        "op add __tmp4 x 20\n" +
                        "op sub __tmp5 y 20\n" +
                        "draw triangle __tmp0 __tmp1 __tmp2 __tmp3 __tmp4 __tmp5\n" +
                        "end\n",
                LogicInstructionPrinter.toString(getInstructionProcessor(),
                        LogicInstructionLabelResolver.resolve(
                                getInstructionProcessor(),
                                generateUnoptimized(
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
                        "print \"capacity: \"\n" +
                        "print capacity\n" +
                        "print \"\\n\"\n" +
                        "set n 0\n" +
                        "op lessThan __tmp1 n @links\n" +
                        "jump 43 equal __tmp1 false\n" +
                        "getlink __tmp2 n\n" +
                        "set building __tmp2\n" +
                        "sensor __tmp3 building @type\n" +
                        "set type __tmp3\n" +
                        "op equal __tmp4 type @conveyor\n" +
                        "op equal __tmp5 type @titanium-conveyor\n" +
                        "op or __tmp6 __tmp4 __tmp5\n" +
                        "op equal __tmp7 type @plastanium-conveyor\n" +
                        "op or __tmp8 __tmp6 __tmp7\n" +
                        "jump 39 equal __tmp8 false\n" +
                        "sensor __tmp10 building @firstItem\n" +
                        "set resource __tmp10\n" +
                        "op notEqual __tmp11 resource null\n" +
                        "jump 36 equal __tmp11 false\n" +
                        "sensor __tmp13 nucleus1 @resource\n" +
                        "set level __tmp13\n" +
                        "op lessThan __tmp14 level capacity\n" +
                        "control enabled building __tmp14 0 0 0\n" +
                        "print \"\\n\"\n" +
                        "print n\n" +
                        "print \": \"\n" +
                        "print resource\n" +
                        "print \" @ \"\n" +
                        "print level\n" +
                        "set __tmp12 level\n" +
                        "jump 37 always 0 0\n" +
                        "set __tmp12 null\n" +
                        "set __tmp9 __tmp12\n" +
                        "jump 40 always 0 0\n" +
                        "set __tmp9 null\n" +
                        "op add __tmp15 n 1\n" +
                        "set n __tmp15\n" +
                        "jump 8 always 0 0\n" +
                        "printflush MSG\n" +
                        "end\n",
                LogicInstructionPrinter.toString(getInstructionProcessor(),
                        LogicInstructionLabelResolver.resolve(
                                getInstructionProcessor(),
                                generateUnoptimized(
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
