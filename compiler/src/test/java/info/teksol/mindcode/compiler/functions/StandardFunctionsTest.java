package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

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
                        (Seq) translateToAst("""
                                clear(r, g, b)
                                color(r, g, b, alpha)
                                stroke(width)
                                line(x1, y1, x2, y2)
                                rect(x, y, w, h)
                                lineRect(x, y, w, h)
                                poly(x, y, sides, radius, rotation)
                                linePoly(x, y, sides, radius, rotation)
                                triangle(x1, y1, x2, y2, x3, y3)
                                image(x, y, @copper, size, rotation)
                                drawflush(display1)
                                """
                        )
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
                        createInstruction(JUMP, var(1000), "equal", var(1), "false"),
                        createInstruction(SENSOR, var(3), "target", "@x"),
                        createInstruction(SENSOR, var(4), "target", "@y"),
                        createInstruction(UCONTROL, "approach", var(3), var(4), "10"),
                        createInstruction(SENSOR, var(5), "target", "@x"),
                        createInstruction(SENSOR, var(6), "target", "@y"),
                        createInstruction(UCONTROL, "within", var(5), var(6), "10", var(7)),
                        createInstruction(JUMP, var(1002), "equal", var(7), "false"),
                        createInstruction(SENSOR, var(9), "target", "@x"),
                        createInstruction(SENSOR, var(10), "target", "@y"),
                        createInstruction(UCONTROL, "target", var(9), var(10), "SHOOT"),
                        createInstruction(SET, var(8), "null"),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(8), "null"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(2), var(8)),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(2), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                target = uradar(enemy, ground, any, health, MIN_TO_MAX)
                                if target != null
                                    approach(target.x, target.y, 10)
                                    if within(target.x, target.y, 10)
                                        target(target.x, target.y, SHOOT)
                                    end
                                end
                                """
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
                        (Seq) translateToAst("""
                                ulocate(ore, @surge-alloy, outx, outy)
                                ulocate(building, core, ENEMY, outx, outy, outbuilding)
                                ulocate(spawn, outx, outy, outbuilding)
                                ulocate(damaged, outx, outy, outbuilding)
                                """
                        )
                )
        );
    }

    @Test
    void generatesRadar() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(RADAR, "enemy", "any", "any", "distance", "salvo1", "1", var(0)),
                        createInstruction(SET, "out", var(0)),
                        createInstruction(RADAR, "ally", "flying", "any", "health", "lancer1", "1", var(1)),
                        createInstruction(SET, "out", var(1)),
                        createInstruction(SET, "src", "salvo1"),
                        createInstruction(RADAR, "enemy", "any", "any", "distance", "src", "1", var(2)),
                        createInstruction(SET, "out", var(2)),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                out = radar(enemy, any, any, distance, salvo1, 1)
                                out = radar(ally, flying, any, health, lancer1, 1)
                                src = salvo1
                                out = radar(enemy, any, any, distance, src, 1)
                                """
                        )
                )
        );
    }

    @Test
    void generatesWait() {
        setInstructionProcessor(InstructionProcessorFactory.getInstructionProcessor(ProcessorVersion.V7, ProcessorEdition.STANDARD_PROCESSOR));
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(WAIT, "1"),
                        createInstruction(WAIT, "0.001"),
                        createInstruction(WAIT, "1000"),
                        createInstruction(END)
                ),
                generateUnoptimized(
                        (Seq) translateToAst("""
                                wait(1)
                                wait(0.001)
                                wait(1000)
                                """
                        )
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
                generateAndOptimize(
                        (Seq) translateToAst("""
                                col(color)
                                result = asin(a)
                                result = acos(a)
                                result = atan(a)
                                result = lookup(block, index)
                                result = packcolor(r, g, b, a)
                                wait(sec)
                                payEnter()
                                unbind()
                                getBlock(x, y, type, building, floor)
                                print(result, type)
                                """
                        )
                )
        );
    }
}
