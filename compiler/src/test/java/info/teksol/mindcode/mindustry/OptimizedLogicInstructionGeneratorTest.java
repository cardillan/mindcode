package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OptimizedLogicInstructionGeneratorTest extends AbstractGeneratorTest {
    @Test
    void correctlyOptimizesFunctionCallAndReturn() {
       /* VERY USEFUL FOR DEBUGGING PURPOSES -- the two files can be compared using the diff(1)
        try (final Writer w = new FileWriter("unoptimized.txt")) {
            w.write(
                    LogicInstructionPrinter.toString(
                            generateUnoptimized(
                                    (Seq) translateToAst(
                                            "allocate stack in cell1[33..48], heap in cell2[3...7]\ndef fn(n)\n2 * n\nend\n\n$x = fn(4) + fn(5)\n$y = $x + 1\n"
                                    )
                            )
                    )
            );
        }

        try (final Writer w = new FileWriter("optimized.txt")) {
            w.write(
                    LogicInstructionPrinter.toString(
                            generateAndOptimize(
                                    (Seq) translateToAst(
                                            "allocate stack in cell1[33..48], heap in cell2[3...7]\ndef fn(n)\n2 * n\nend\n\n$x = fn(4) + fn(5)\n$y = $x + 1\n"
                                    )
                            )
                    )
            );
        }
        */

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(WRITE, "48", "cell1", "48"),
                        createInstruction(READ, var(3), "cell1", "48"),
                        createInstruction(OP, "sub", var(3), var(3), "1"),
                        createInstruction(WRITE, var(1001), "cell1", var(3)),
                        createInstruction(WRITE, var(3), "cell1", "48"),
                        createInstruction(READ, var(9), "cell1", "48"),
                        createInstruction(OP, "sub", var(9), var(9), "1"),
                        createInstruction(WRITE, "4", "cell1", var(9)),
                        createInstruction(WRITE, var(9), "cell1", "48"),
                        createInstruction(SET, "@counter", var(1000)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(READ, var(16), "cell1", "48"),
                        createInstruction(READ, var(15), "cell1", var(16)),
                        createInstruction(OP, "add", var(16), var(16), "1"),
                        createInstruction(WRITE, var(16), "cell1", "48"),
                        createInstruction(READ, var(24), "cell1", "48"),
                        createInstruction(OP, "sub", var(24), var(24), "1"),
                        createInstruction(WRITE, var(1002), "cell1", var(24)),
                        createInstruction(WRITE, var(24), "cell1", "48"),
                        createInstruction(READ, var(30), "cell1", "48"),
                        createInstruction(OP, "sub", var(30), var(30), "1"),
                        createInstruction(WRITE, "5", "cell1", var(30)),
                        createInstruction(WRITE, var(30), "cell1", "48"),
                        createInstruction(SET, "@counter", var(1000)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(READ, var(37), "cell1", "48"),
                        createInstruction(READ, var(36), "cell1", var(37)),
                        createInstruction(OP, "add", var(37), var(37), "1"),
                        createInstruction(WRITE, var(37), "cell1", "48"),
                        createInstruction(OP, "add", var(44), var(15), var(36)),
                        createInstruction(WRITE, var(44), "cell2", "3"),
                        createInstruction(READ, var(47), "cell2", "3"),
                        createInstruction(OP, "add", var(49), var(47), "1"),
                        createInstruction(WRITE, var(49), "cell2", "4"),

                        createInstruction(END),

                        createInstruction(LABEL, var(1000)),
                        createInstruction(READ, var(52), "cell1", "48"),
                        createInstruction(READ, var(51), "cell1", var(52)),
                        createInstruction(OP, "add", var(52), var(52), "1"),
                        createInstruction(WRITE, var(52), "cell1", "48"),
                        createInstruction(SET, "n", var(51)),
                        createInstruction(OP, "mul", var(60), "2", "n"),
                        createInstruction(READ, var(62), "cell1", "48"),
                        createInstruction(READ, var(61), "cell1", var(62)),
                        createInstruction(OP, "add", var(62), var(62), "1"),
                        createInstruction(WRITE, var(62), "cell1", "48"),
                        createInstruction(READ, var(69), "cell1", "48"),
                        createInstruction(OP, "sub", var(69), var(69), "1"),
                        createInstruction(WRITE, var(60), "cell1", var(69)),
                        createInstruction(WRITE, var(69), "cell1", "48"),
                        createInstruction(SET, "@counter", var(61)),

                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst(
                                "allocate stack in cell1[33..48], heap in cell2[3...7]\ndef fn(n)\n2 * n\nend\n\n$x = fn(4) + fn(5)\n$y = $x + 1\n"
                        )
                )
        );
    }

    @Test
    void optimizeSetThenOpWithBinaryOpBoth() {
        final List<LogicInstruction> result = generateAndOptimize(
                (Seq) translateToAst(
                        "x = 41\ny = 72\npos = x + y\nmove(40, pos)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "x", "41"),
                        createInstruction(SET, "y", "72"),
                        createInstruction(OP, "add", "pos", "x", "y"),
                        createInstruction(UCONTROL, "move", "40", "pos"),
                        createInstruction(END)
                ),
                result
        );
    }

    @Test
    void setThenReadPrefersUserSpecifiedNames() {
        final List<LogicInstruction> result = generateAndOptimize(
                (Seq) translateToAst(
                        "" +
                                "addr_FLAG = 0\n" +
                                "conveyor1.enabled = cell1[addr_FLAG] == 0\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "addr_FLAG", "0"),
                        createInstruction(READ, var(0), "cell1", "addr_FLAG"),
                        createInstruction(OP, "equal", var(1), var(0), "0"),
                        createInstruction(CONTROL, "enabled", "conveyor1", var(1)),
                        createInstruction(END)
                ),
                result
        );
    }

    @Test
    void reallifeScripts() {
        final List<LogicInstruction> result = generateAndOptimize(
                (Seq) translateToAst(
                        "" +
                                "silicon = reconstructor1.silicon\n" +
                                "graphite = reconstructor1.graphite\n" +
                                "capacity = reconstructor1.itemCapacity\n" +
                                "\n" +
                                "conveyor1.enabled = !( silicon < capacity || graphite < capacity )\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "silicon", "reconstructor1", "@silicon"),
                        createInstruction(SENSOR, "graphite", "reconstructor1", "@graphite"),
                        createInstruction(SENSOR, "capacity", "reconstructor1", "@itemCapacity"),
                        createInstruction(OP, "lessThan", var(3), "silicon", "capacity"),
                        createInstruction(OP, "lessThan", var(4), "graphite", "capacity"),
                        createInstruction(OP, "or", var(5), var(3), var(4)),
                        createInstruction(OP, "equal", var(6), var(5), "false"),
                        createInstruction(CONTROL, "enabled", "conveyor1", var(6)),
                        createInstruction(END)
                ),
                result
        );
    }

    @Test
    void reallifeScripts2() {
        final List<LogicInstruction> result = generateAndOptimize(
                (Seq) translateToAst(
                        "" +
                                "level = nucleus1.resource\n" +
                                "print(level)\n" +
                                "building.enabled = level < capacity"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "level", "nucleus1", "@resource"),
                        createInstruction(PRINT, "level"),
                        createInstruction(OP, "lessThan", var(0), "level", "capacity"),
                        createInstruction(CONTROL, "enabled", "building", var(0)),
                        createInstruction(END)
                ),
                result
        );
    }

    @Test
    void regressionTest1() {
        // https://github.com/francois/mindcode/issues/13
        final List<LogicInstruction> result = generateAndOptimize(
                (Seq) translateToAst(
                        "" +
                                "// Source\n" +
                                "HEAPPTR = cell3\n" +
                                "allocate heap in HEAPPTR\n" +
                                "$a = 1\n" +
                                "$b = 2\n" +
                                "$c = 3\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "HEAPPTR", "cell3"),
                        createInstruction(WRITE, "1", "HEAPPTR", "0"),
                        createInstruction(WRITE, "2", "HEAPPTR", "1"),
                        createInstruction(WRITE, "3", "HEAPPTR", "2"),
                        createInstruction(END)
                ),
                result
        );
    }

    @Test
    void regressionTest2() {
        // https://github.com/francois/mindcode/issues/13
        final List<LogicInstruction> result = generateAndOptimize(
                (Seq) translateToAst(
                        "" +
                                "HEAPPTR = cell3\n" +
                                "allocate heap in HEAPPTR\n" +
                                "print($a)\n" +
                                "$a = 1\n" +
                                "$b = 2\n" +
                                "$c = 3\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "HEAPPTR", "cell3"),
                        createInstruction(READ, var(0), "HEAPPTR", "0"),
                        createInstruction(PRINT, var(0)),
                        createInstruction(WRITE, "1", "HEAPPTR", "0"),
                        createInstruction(WRITE, "2", "HEAPPTR", "1"),
                        createInstruction(WRITE, "3", "HEAPPTR", "2"),
                        createInstruction(END)
                ),
                result
        );
    }

    @Test
    void regressionTest3() {
        // https://github.com/francois/mindcode/issues/15

        final List<LogicInstruction> result = generateAndOptimize(
                (Seq) translateToAst("" +
                        "desired = @dagger\n" +
                        "boosting = false\n" +
                        "payTake(desired)\n" +
                        "payDrop()\n" +
                        "boost(boosting)\n" +
                        "pathfind()\n" +
                        "idle()\n" +
                        "stop()"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "desired", "@dagger"),
                        createInstruction(SET, "boosting", "false"),
                        createInstruction(UCONTROL, "payTake", "desired"),
                        createInstruction(UCONTROL, "payDrop"),
                        createInstruction(UCONTROL, "boost", "boosting"),
                        createInstruction(UCONTROL, "pathfind"),
                        createInstruction(UCONTROL, "idle"),
                        createInstruction(UCONTROL, "stop"),
                        createInstruction(END)
                ),
                result
        );
    }

    @Test
    void regressionTest4() {
        // https://github.com/francois/mindcode/issues/23
        final List<LogicInstruction> result = generateAndOptimize(
                (Seq) translateToAst("" +
                        "x = 1\n" +
                        "print(\"\\nx: \", x)\n" +
                        "print(\"\\nx+x: \", x+x)")
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "x", "1"),
                        createInstruction(PRINT, "\"\\nx: \""),
                        createInstruction(PRINT, "x"),
                        createInstruction(OP, "add", var(3), "x", "x"),
                        createInstruction(PRINT, "\"\\nx+x: \""),
                        createInstruction(PRINT, var(3)),
                        createInstruction(END)
                ),
                result
        );
    }

    @Test
    void regressionTest5() {
        // Fix "Unvisited opcode: draw" and "drawflush"
        // We don't actually care about the generated code, only that it doesn't raise
        // Otherwise, this spec would be at the mercy of any improvements in the peephole optimizer
        assertDoesNotThrow(() -> {
            generateAndOptimize(
                    (Seq) translateToAst("" +
                            "// move previous values left\n" +
                            "for n in 0 ... 40\n" +
                            "  cell1[n] = cell1[n + 1]\n" +
                            "end\n" +
                            "\n" +
                            "// delay by 1/2 a sec (0.5 s)\n" +
                            "// this depends on your framerate -- if less than 60 fps,\n" +
                            "// the delay will be longer than 0.5s\n" +
                            "deadline = @tick + 30\n" +
                            "while @tick < deadline\n" +
                            "  n += 1\n" +
                            "end\n" +
                            "\n" +
                            "// calculate the new value -- the rightmost one\n" +
                            "// change this line to graph another level\n" +
                            "cell1[39] = tank1.cryofluid / tank1.liquidCapacity\n" +
                            "\n" +
                            "// draw the graph\n" +
                            "\n" +
                            "// clear the display\n" +
                            "clear(0, 0, 0)\n" +
                            "\n" +
                            "// set the foreground color to cryofluid\n" +
                            "color(62, 207, 240, 255)\n" +
                            "\n" +
                            "// draw the bar graph\n" +
                            "for n in 0 ... 40\n" +
                            "  rect(2 * n, 0, 2, 80 * cell1[n])\n" +
                            "end\n" +
                            "\n" +
                            "drawflush(display1)")
            );
        });
    }

    @Test
    void regressionTest6() {
        // https://github.com/francois/mindcode/issues/32
        final List<LogicInstruction> result = generateAndOptimize(
                (Seq) translateToAst("" +
                        "TestVar = 0xf\n" +
                        "Result = ~TestVar\n" +
                        "print(TestVar, \"\\n\", Result)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "TestVar", "0xf"),
                        createInstruction(OP, "not", "Result", "TestVar"),
                        createInstruction(PRINT, "TestVar"),
                        createInstruction(PRINT, "\"\\n\""),
                        createInstruction(PRINT, "Result"),
                        createInstruction(END)
                ),
                result
        );
    }
}
