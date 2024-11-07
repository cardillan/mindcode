package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

class StandardFunctionsTest extends AbstractGeneratorTest {

    @Test
    void generatesDrawings() {
        assertCompilesTo("""
                        clear(r, g, b);
                        color(r, g, b, alpha);
                        stroke(width);
                        line(x1, y1, x2, y2);
                        rect(x, y, w, h);
                        lineRect(x, y, w, h);
                        poly(x, y, sides, radius, rotation);
                        linePoly(x, y, sides, radius, rotation);
                        triangle(x1, y1, x2, y2, x3, y3);
                        image(x, y, @copper, size, rotation);
                        drawflush(display1);
                        """,
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
        );
    }

    @Test
    void generatesURadar() {
        assertCompilesTo("""
                        target = uradar(enemy, ground, any, health, MIN_TO_MAX);
                        if target != null then
                            approach(target.@x, target.@y, 10);
                            if within(target.@x, target.@y, 10) then
                                target(target.@x, target.@y, SHOOT);
                            end;
                        end;
                        """,
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
        );
    }

    @Test
    void generatesULocate() {
        assertCompilesTo("""
                        ulocate(ore, @surge-alloy, out x, out y);
                        building = ulocate(building, core, ENEMY, out x, out y);
                        building = ulocate(spawn, out x, out y);
                        building = ulocate(damaged, out x, out y);
                        """,
                createInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", "x", "y", var(0), var(1)),
                createInstruction(ULOCATE, "building", "core", "ENEMY", "@copper", "x", "y", var(3), var(2)),
                createInstruction(SET, "building", var(2)),
                createInstruction(ULOCATE, "spawn", "core", "true", "@copper", "x", "y", var(5), var(4)),
                createInstruction(SET, "building", var(4)),
                createInstruction(ULOCATE, "damaged", "core", "true", "@copper", "x", "y", var(7), var(6)),
                createInstruction(SET, "building", var(6)),
                createInstruction(END)
        );
    }

    @Test
    void generatesRadar() {
        assertCompilesTo("""
                        block = radar(enemy, any, any, distance, salvo1, 1);
                        block = radar(ally, flying, any, health, lancer1, 1);
                        src = salvo1;
                        block = radar(enemy, any, any, distance, src, 1);
                        """,
                createInstruction(RADAR, "enemy", "any", "any", "distance", "salvo1", "1", var(0)),
                createInstruction(SET, "block", var(0)),
                createInstruction(RADAR, "ally", "flying", "any", "health", "lancer1", "1", var(1)),
                createInstruction(SET, "block", var(1)),
                createInstruction(SET, "src", "salvo1"),
                createInstruction(RADAR, "enemy", "any", "any", "distance", "src", "1", var(2)),
                createInstruction(SET, "block", var(2)),
                createInstruction(END)
        );
    }

    @Test
    void generatesWait() {
        assertCompilesTo("""
                        wait(1);
                        wait(0.001);
                        wait(1000);
                        """,
                createInstruction(WAIT, "1"),
                createInstruction(WAIT, "0.001"),
                createInstruction(WAIT, "1000"),
                createInstruction(END)
        );
    }

    @Test
    void generatesNewVersion7Instructions() {
        assertCompilesTo("""
                        col(color);
                        result = asin(a);
                        result = acos(a);
                        result = atan(a);
                        result = lookup(block, index);
                        result = packcolor(r, g, b, a);
                        wait(sec);
                        payEnter();
                        unbind();
                        building = getBlock(x, y);
                        print(building, result);
                        """,
                createInstruction(DRAW, "col", "color"),
                createInstruction(OP, "asin", var(0), "a"),
                createInstruction(SET, "result", var(0)),
                createInstruction(OP, "acos", var(1), "a"),
                createInstruction(SET, "result", var(1)),
                createInstruction(OP, "atan", var(2), "a"),
                createInstruction(SET, "result", var(2)),
                createInstruction(LOOKUP, "block", var(3), "index"),
                createInstruction(SET, "result", var(3)),
                createInstruction(PACKCOLOR, var(4), "r", "g", "b", "a"),
                createInstruction(SET, "result", var(4)),
                createInstruction(WAIT, "sec"),
                createInstruction(UCONTROL, "payEnter"),
                createInstruction(UCONTROL, "unbind"),
                createInstruction(UCONTROL, "getBlock", "x", "y", var(6), var(5), var(7)),
                createInstruction(SET, "building", var(5)),
                createInstruction(PRINT, "building"),
                createInstruction(PRINT, "result"),
                createInstruction(END)
        );
    }

    @Test
    void generatesNewPathfind() {
        assertCompilesTo("""
                        pathfind(50, 50);
                        """,
                createInstruction(UCONTROL, "pathfind", "50", "50"),
                createInstruction(END)
        );
    }

    @Test
    void generatesNewAutoPathfind() {
        assertCompilesTo("""
                        autoPathfind();
                        """,
                createInstruction(UCONTROL, "autoPathfind"),
                createInstruction(END)
        );
    }
}
