package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OptimizedLogicInstructionGeneratorTest extends AbstractGeneratorTest {
    @Test
    void correctlyOptimizesFunctionCallAndReturn() {
       /* VERY USEFUL FOR DEBUGGING PURPOSES -- the two files can be compared using the diff(1)
        try (final Writer w = new FileWriter("unoptimized.txt")) {
            w.write(
                    LogicInstructionPrinter.toString(
                            LogicInstructionGenerator.generateUnoptimized(
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
                            LogicInstructionGenerator.generateAndOptimize(
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
                        new LogicInstruction("write", "48", "cell1", "48"),
                        new LogicInstruction("read", var(3), "cell1", "48"),
                        new LogicInstruction("op", "sub", var(3), var(3), "1"),
                        new LogicInstruction("write", var(1001), "cell1", var(3)),
                        new LogicInstruction("write", var(3), "cell1", "48"),
                        new LogicInstruction("read", var(9), "cell1", "48"),
                        new LogicInstruction("op", "sub", var(9), var(9), "1"),
                        new LogicInstruction("write", "4", "cell1", var(9)),
                        new LogicInstruction("write", var(9), "cell1", "48"),
                        new LogicInstruction("set", "@counter", var(1000)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("read", var(16), "cell1", "48"),
                        new LogicInstruction("read", var(15), "cell1", var(16)),
                        new LogicInstruction("op", "add", var(16), var(16), "1"),
                        new LogicInstruction("write", var(16), "cell1", "48"),
                        new LogicInstruction("read", var(24), "cell1", "48"),
                        new LogicInstruction("op", "sub", var(24), var(24), "1"),
                        new LogicInstruction("write", var(1002), "cell1", var(24)),
                        new LogicInstruction("write", var(24), "cell1", "48"),
                        new LogicInstruction("read", var(30), "cell1", "48"),
                        new LogicInstruction("op", "sub", var(30), var(30), "1"),
                        new LogicInstruction("write", "5", "cell1", var(30)),
                        new LogicInstruction("write", var(30), "cell1", "48"),
                        new LogicInstruction("set", "@counter", var(1000)),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("read", var(37), "cell1", "48"),
                        new LogicInstruction("read", var(36), "cell1", var(37)),
                        new LogicInstruction("op", "add", var(37), var(37), "1"),
                        new LogicInstruction("write", var(37), "cell1", "48"),
                        new LogicInstruction("op", "add", var(44), var(15), var(36)),
                        new LogicInstruction("write", var(44), "cell2", "3"),
                        new LogicInstruction("read", var(47), "cell2", "3"),
                        new LogicInstruction("op", "add", var(49), var(47), "1"),
                        new LogicInstruction("write", var(49), "cell2", "4"),

                        new LogicInstruction("end"),

                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("read", var(52), "cell1", "48"),
                        new LogicInstruction("read", var(51), "cell1", var(52)),
                        new LogicInstruction("op", "add", var(52), var(52), "1"),
                        new LogicInstruction("write", var(52), "cell1", "48"),
                        new LogicInstruction("set", "n", var(51)),
                        new LogicInstruction("op", "mul", var(60), "2", "n"),
                        new LogicInstruction("read", var(62), "cell1", "48"),
                        new LogicInstruction("read", var(61), "cell1", var(62)),
                        new LogicInstruction("op", "add", var(62), var(62), "1"),
                        new LogicInstruction("write", var(62), "cell1", "48"),
                        new LogicInstruction("read", var(69), "cell1", "48"),
                        new LogicInstruction("op", "sub", var(69), var(69), "1"),
                        new LogicInstruction("write", var(60), "cell1", var(69)),
                        new LogicInstruction("write", var(69), "cell1", "48"),
                        new LogicInstruction("set", "@counter", var(61)),

                        new LogicInstruction("end")
                ),
                LogicInstructionGenerator.generateAndOptimize(
                        (Seq) translateToAst(
                                "allocate stack in cell1[33..48], heap in cell2[3...7]\ndef fn(n)\n2 * n\nend\n\n$x = fn(4) + fn(5)\n$y = $x + 1\n"
                        )
                )
        );
    }

    @Test
    void optimizeSetThenOpWithBinaryOpBoth() {
        final List<LogicInstruction> result = LogicInstructionGenerator.generateAndOptimize(
                (Seq) translateToAst(
                        "x = 41\ny = 72\npos = x + y\nmove(40, pos)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "x", "41"),
                        new LogicInstruction("set", "y", "72"),
                        new LogicInstruction("op", "add", "pos", "x", "y"),
                        new LogicInstruction("ucontrol", "move", "40", "pos"),
                        new LogicInstruction("end")
                ),
                result
        );
    }

    @Test
    void setThenReadPrefersUserSpecifiedNames() {
        final List<LogicInstruction> result = LogicInstructionGenerator.generateAndOptimize(
                (Seq) translateToAst(
                        "" +
                                "addr_FLAG = 0\n" +
                                "conveyor1.enabled = cell1[addr_FLAG] == 0\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "addr_FLAG", "0"),
                        new LogicInstruction("read", var(0), "cell1", "addr_FLAG"),
                        new LogicInstruction("op", "equal", var(1), var(0), "0"),
                        new LogicInstruction("control", "enabled", "conveyor1", var(1)),
                        new LogicInstruction("end")
                ),
                result
        );
    }

    @Test
    void reallifeScripts() {
        final List<LogicInstruction> result = LogicInstructionGenerator.generateAndOptimize(
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
                        new LogicInstruction("sensor", "silicon", "reconstructor1", "@silicon"),
                        new LogicInstruction("sensor", "graphite", "reconstructor1", "@graphite"),
                        new LogicInstruction("sensor", "capacity", "reconstructor1", "@itemCapacity"),
                        new LogicInstruction("op", "lessThan", var(3), "silicon", "capacity"),
                        new LogicInstruction("op", "lessThan", var(4), "graphite", "capacity"),
                        new LogicInstruction("op", "or", var(5), var(3), var(4)),
                        new LogicInstruction("op", "not", var(6), var(5)),
                        new LogicInstruction("control", "enabled", "conveyor1", var(6)),
                        new LogicInstruction("end")
                ),
                result
        );
    }

    @Test
    void reallifeScripts2() {
        final List<LogicInstruction> result = LogicInstructionGenerator.generateAndOptimize(
                (Seq) translateToAst(
                        "" +
                                "level = nucleus1.resource\n" +
                                "print(level)\n" +
                                "building.enabled = level < capacity"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "level", "nucleus1", "@resource"),
                        new LogicInstruction("print", "level"),
                        new LogicInstruction("op", "lessThan", var(0), "level", "capacity"),
                        new LogicInstruction("control", "enabled", "building", var(0)),
                        new LogicInstruction("end")
                ),
                result
        );
    }

    @Test
    void regressionTest1() {
        // https://github.com/francois/mindcode/issues/13
        final List<LogicInstruction> result = LogicInstructionGenerator.generateAndOptimize(
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
                        new LogicInstruction("set", "HEAPPTR", "cell3"),
                        new LogicInstruction("write", "1", "HEAPPTR", "0"),
                        new LogicInstruction("write", "2", "HEAPPTR", "1"),
                        new LogicInstruction("write", "3", "HEAPPTR", "2"),
                        new LogicInstruction("end")
                ),
                result
        );
    }

    @Test
    void regressionTest2() {
        // https://github.com/francois/mindcode/issues/13
        final List<LogicInstruction> result = LogicInstructionGenerator.generateAndOptimize(
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
                        new LogicInstruction("set", "HEAPPTR", "cell3"),
                        new LogicInstruction("read", var(0), "HEAPPTR", "0"),
                        new LogicInstruction("print", var(0)),
                        new LogicInstruction("write", "1", "HEAPPTR", "0"),
                        new LogicInstruction("write", "2", "HEAPPTR", "1"),
                        new LogicInstruction("write", "3", "HEAPPTR", "2"),
                        new LogicInstruction("end")
                ),
                result
        );
    }

    @Test
    void regressionTest3() {
        // https://github.com/francois/mindcode/issues/15

        final List<LogicInstruction> result = LogicInstructionGenerator.generateAndOptimize(
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
                        new LogicInstruction("set", "desired", "@dagger"),
                        new LogicInstruction("set", "boosting", "false"),
                        new LogicInstruction("ucontrol", "payTake", "desired"),
                        new LogicInstruction("ucontrol", "payDrop"),
                        new LogicInstruction("ucontrol", "boost", "boosting"),
                        new LogicInstruction("ucontrol", "pathfind"),
                        new LogicInstruction("ucontrol", "idle"),
                        new LogicInstruction("ucontrol", "stop"),
                        new LogicInstruction("end")
                ),
                result
        );
    }

    @Test
    void regressionTest4() {
        // https://github.com/francois/mindcode/issues/23
        final List<LogicInstruction> result = LogicInstructionGenerator.generateAndOptimize(
                (Seq) translateToAst("" +
                        "x = 1\n" +
                        "print(\"\\nx: \", x)\n" +
                        "print(\"\\nx+x: \", x+x)")
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "x", "1"),
                        new LogicInstruction("print", "\"\\nx: \""),
                        new LogicInstruction("print", "x"),
                        new LogicInstruction("op", "add", var(3), "x", "x"),
                        new LogicInstruction("print", "\"\\nx+x: \""),
                        new LogicInstruction("print", var(3)),
                        new LogicInstruction("end")
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
            LogicInstructionGenerator.generateAndOptimize(
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
        final List<LogicInstruction> result = LogicInstructionGenerator.generateAndOptimize(
                (Seq) translateToAst("" +
                        "TestVar = 0xf\n" +
                        "Result = !TestVar\n" +
                        "print(TestVar, \"\\n\", Result)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "TestVar", "0xf"),
                        new LogicInstruction("op", "not", "Result", "TestVar"),
                        new LogicInstruction("print", "TestVar"),
                        new LogicInstruction("print", "\"\\n\""),
                        new LogicInstruction("print", "Result"),
                        new LogicInstruction("end")
                ),
                result
        );
    }
}
