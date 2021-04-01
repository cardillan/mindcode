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
                "set tmp0 20\n" +
                        "op sub tmp1 x tmp0\n" +
                        "set tmp2 20\n" +
                        "op sub tmp3 y tmp2\n" +
                        "set tmp4 20\n" +
                        "op add tmp5 x tmp4\n" +
                        "set tmp6 20\n" +
                        "op sub tmp7 y tmp6\n" +
                        "set tmp8 20\n" +
                        "op add tmp9 x tmp8\n" +
                        "set tmp10 20\n" +
                        "op sub tmp11 y tmp10\n" +
                        "draw triangle tmp1 tmp3 tmp5 tmp7 tmp9 tmp11\n" +
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
                        "sensor tmp0 STORAGE @itemCapacity\n" +
                        "set capacity tmp0\n" +
                        "set tmp1 \"capacity: \"\n" +
                        "set tmp2 \"\\n\"\n" +
                        "print tmp1\n" +
                        "print capacity\n" +
                        "print tmp2\n" +
                        "set tmp3 0\n" +
                        "set n tmp3\n" +
                        "op lessThan tmp4 n @links\n" +
                        "jump 50 notEqual tmp4 true\n" +
                        "getlink tmp5 n\n" +
                        "set building tmp5\n" +
                        "sensor tmp6 building @type\n" +
                        "set type tmp6\n" +
                        "op equal tmp7 type @conveyor\n" +
                        "op equal tmp8 type @titanium-conveyor\n" +
                        "op or tmp9 tmp7 tmp8\n" +
                        "op equal tmp10 type @plastanium-conveyor\n" +
                        "op or tmp11 tmp9 tmp10\n" +
                        "jump 45 notEqual tmp11 true\n" +
                        "sensor tmp13 building @firstItem\n" +
                        "set resource tmp13\n" +
                        "op notEqual tmp14 resource null\n" +
                        "jump 42 notEqual tmp14 true\n" +
                        "sensor tmp16 nucleus1 @resource\n" +
                        "set level tmp16\n" +
                        "op lessThan tmp17 level capacity\n" +
                        "control enabled building tmp17 0 0 0\n" +
                        "set tmp18 \"\\n\"\n" +
                        "set tmp19 \": \"\n" +
                        "set tmp20 \" @ \"\n" +
                        "print tmp18\n" +
                        "print n\n" +
                        "print tmp19\n" +
                        "print resource\n" +
                        "print tmp20\n" +
                        "print level\n" +
                        "set tmp15 level\n" +
                        "jump 43 always 0 0\n" +
                        "set tmp15 null\n" +
                        "set tmp12 tmp15\n" +
                        "jump 46 always 0 0\n" +
                        "set tmp12 null\n" +
                        "set tmp21 1\n" +
                        "op add tmp22 n tmp21\n" +
                        "set n tmp22\n" +
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
