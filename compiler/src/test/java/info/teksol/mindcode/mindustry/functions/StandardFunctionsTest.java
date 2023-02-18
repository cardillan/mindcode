package info.teksol.mindcode.mindustry.functions;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.mindustry.logic.ProcessorEdition;
import info.teksol.mindcode.mindustry.logic.ProcessorVersion;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

class StandardFunctionsTest extends AbstractGeneratorTest {

    @Test
    void generatesDrawings() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(DRAW, "clear", "r", "g", "b"),
                        createInstruction(DRAW, "color", "r", "g", "b", "alpha"),
                        createInstruction(DRAW, "stroke", "width"),
                        createInstruction(DRAW, "line", "x1", "y1", "x2", "y2"),
                        createInstruction(DRAW, "rect", "x", "y", "w", "h"),
                        createInstruction(DRAW, "lineRect", "x", "y", "w", "h"),
                        createInstruction(DRAW, "poly", "x", "y", "sides", "radius", "rotation"),
                        createInstruction(DRAW, "linePoly", "x", "y", "sides", "radius", "rotation"),
                        createInstruction(DRAW, "triangle", "x1", "y1", "x2", "y2", "x3", "y3"),
                        createInstruction(DRAW, "image", "x", "y", "@copper", "size", "rotation"),
                        createInstruction(DRAWFLUSH, "display1"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("clear(r, g, b)\ncolor(r, g, b, alpha)\nstroke(width)\nline(x1, y1, x2, y2)\nrect(x, y, w, h)\nlineRect(x, y, w, h)\npoly(x, y, sides, radius, rotation)\nlinePoly(x, y, sides, radius, rotation)\ntriangle(x1, y1, x2, y2, x3, y3)\nimage(x, y, @copper, size, rotation)\ndrawflush(display1)\n")
                )
        );
    }

    @Test
    void generatesURadar() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(URADAR, "enemy", "ground", "any", "health", "0", "MIN_TO_MAX", var(0)),
                        createInstruction(SET, "target", var(0)),
                        createInstruction(OP, "notEqual", var(1), "target", "null"),
                        createInstruction(JUMP, var(1002), "equal", var(1), "false"),
                        createInstruction(SENSOR, var(2), "target", "@x"),
                        createInstruction(SENSOR, var(3), "target", "@y"),
                        createInstruction(SET, var(4), "10"),
                        createInstruction(UCONTROL, "approach", var(2), var(3), var(4)),
                        createInstruction(SENSOR, var(5), "target", "@x"),
                        createInstruction(SENSOR, var(6), "target", "@y"),
                        createInstruction(SET, var(7), "10"),
                        createInstruction(UCONTROL, "within", var(5), var(6), var(7), var(8)),
                        createInstruction(JUMP, var(1000), "equal", var(8), "false"),
                        createInstruction(SENSOR, var(9), "target", "@x"),
                        createInstruction(SENSOR, var(10), "target", "@y"),
                        createInstruction(UCONTROL, "target", var(9), var(10), "SHOOT"),
                        createInstruction(SET, var(11), "null"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(11), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(12), var(11)),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(12), "null"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "target = uradar(enemy, ground, any, health, MIN_TO_MAX)\n"
                                + "if target != null\n"
                                + "  approach(target.x, target.y, 10)\n"
                                + "  if within(target.x, target.y, 10)\n"
                                + "    target(target.x, target.y, SHOOT)\n"
                                + "  end\n"
                                + "end\n"
                        )
                )
        );
    }

    @Test
    void generatesULocate() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", "outx", "outy", var(0), var(1)),
                        createInstruction(ULOCATE, "building", "core", "ENEMY", "@copper", "outx", "outy", var(2), "outbuilding"),
                        createInstruction(ULOCATE, "spawn", "core", "true", "@copper", "outx", "outy", var(3), "outbuilding"),
                        createInstruction(ULOCATE, "damaged", "core", "true", "@copper", "outx", "outy", var(4), "outbuilding"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "ulocate(ore, @surge-alloy, outx, outy)\n"
                                + "ulocate(building, core, ENEMY, outx, outy, outbuilding)\n"
                                + "ulocate(spawn, outx, outy, outbuilding)\n"
                                + "ulocate(damaged, outx, outy, outbuilding)\n"
                        )
                )
        );
    }

    @Test
    void generatesRadar() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "1"),
                        createInstruction(RADAR, "enemy", "any", "any", "distance", "salvo1", var(0), var(1)),
                        createInstruction(SET, "out", var(1)),
                        createInstruction(SET, var(2), "1"),
                        createInstruction(RADAR, "ally", "flying", "any", "health", "lancer1", var(2), var(3)),
                        createInstruction(SET, "out", var(3)),
                        createInstruction(SET, "src", "salvo1"),
                        createInstruction(SET, var(4), "1"),
                        createInstruction(RADAR, "enemy", "any", "any", "distance", "src", var(4), var(5)),
                        createInstruction(SET, "out", var(5)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst(""
                                + "out = radar(enemy, any, any, distance, salvo1, 1)\n"
                                + "out = radar(ally, flying, any, health, lancer1, 1)\n"
                                + "src = salvo1\n"
                                + "out = radar(enemy, any, any, distance, src, 1)\n"
                        )
                )
        );
    }

    @Test
    void generatesWait() {
        setInstructionProcessor(InstructionProcessorFactory.getInstructionProcessor(ProcessorVersion.V7, ProcessorEdition.STANDARD_PROCESSOR));
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "1"),
                        createInstruction(WAIT, var(0)),
                        createInstruction(SET, var(1), "0.001"),
                        createInstruction(WAIT, var(1)),
                        createInstruction(SET, var(2), "1000"),
                        createInstruction(WAIT, var(2)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("wait(1)\nwait(0.001)\nwait(1000)")
                )
        );
    }

    @Test
    void generatesNewVersion7Instructions() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(DRAW, "col", "color"),
                        createInstruction(OP, "asin", "result", "a"),
                        createInstruction(OP, "acos", "result", "a"),
                        createInstruction(OP, "atan", "result", "a"),
                        createInstruction(LOOKUP, "block", "result", "index"),
                        createInstruction(PACKCOLOR, "result", "r", "g", "b", "a"),
                        createInstruction(WAIT, "sec"),
                        createInstruction(UCONTROL, "payEnter"),
                        createInstruction(UCONTROL, "unbind"),
                        createInstruction(UCONTROL, "getBlock", "x", "y", "type", "building", "floor"),
                        createInstruction(PRINT, "result"),
                        createInstruction(PRINT, "type"),
                        createInstruction(END)
                ),
                generateAndOptimize((Seq) translateToAst(""
                        + "col(color)\n"
                        + "result = asin(a)\n"
                        + "result = acos(a)\n"
                        + "result = atan(a)\n"
                        + "result = lookup(block, index)\n"
                        + "result = packcolor(r, g, b, a)\n"
                        + "wait(sec)\n"
                        + "payEnter()\n"
                        + "unbind()\n"
                        + "getBlock(x, y, type, building, floor)\n"
                        + "print(result, type)")
                )
        );
    }
}
