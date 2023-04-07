package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.ast.Seq;
import java.io.IOException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OptimizedLogicInstructionGeneratorTest extends AbstractGeneratorTest {

    @Test
    void correctlyOptimizesFunctionCallAndReturn() throws IOException {
        String code = ""
                + "allocate stack in cell1[33..48], heap in cell2[3...7] "
                + "def fn(n) "
                + "    fn(n - 1) "
                + "    2 * n "
                + "end "
                + " "
                + "$x = fn(4) + fn(5) "
                + "$y = $x + 1";

        /* VERY USEFUL FOR DEBUGGING PURPOSES -- the two files can be compared using the diff(1)
        try (final Writer w = new FileWriter("unoptimized.txt")) {
            w.write(
                    LogicInstructionPrinter.toString(
                            getInstructionProcessor(),
                            generateUnoptimized((Seq) translateToAst(code))
                    )
            );
        }

        try (final Writer w = new FileWriter("optimized.txt")) {
            w.write(
                    LogicInstructionPrinter.toString(
                            getInstructionProcessor(),
                            generateAndOptimize((Seq) translateToAst(code))
                    )
            );
        }
         */

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__sp", "33"),
                        createInstruction(SET, "__fn0_n", "4"),
                        createInstruction(CALL, "cell1", var(1000), var(1001)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(1), "__fn0retval"),
                        createInstruction(SET, "__fn0_n", "5"),
                        createInstruction(CALL, "cell1", var(1000), var(1002)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(3), "__fn0retval"),
                        createInstruction(OP, "add", var(4), var(1), var(3)),
                        createInstruction(WRITE, var(4), "cell2", "3"),
                        createInstruction(READ, var(7), "cell2", "3"),
                        createInstruction(OP, "add", var(9), var(7), "1"),
                        createInstruction(WRITE, var(9), "cell2", "4"),
                        createInstruction(END),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "sub", var(12), "__fn0_n", "1"),
                        createInstruction(PUSH, "cell1", "__fn0_n"),
                        createInstruction(SET, "__fn0_n", var(12)),
                        createInstruction(CALL, "cell1", var(1000), var(1004)),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(POP, "cell1", "__fn0_n"),
                        createInstruction(OP, "mul", "__fn0retval", "2", "__fn0_n"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(RETURN, "cell1")
                ),
                generateAndOptimize((Seq) translateToAst(code))
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
                        ""
                        + "addr_FLAG = 0\n"
                        + "conveyor1.enabled = cell1[addr_FLAG] == 0\n"
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
                        ""
                        + "silicon = reconstructor1.silicon\n"
                        + "graphite = reconstructor1.graphite\n"
                        + "capacity = reconstructor1.itemCapacity\n"
                        + "\n"
                        + "conveyor1.enabled = !( silicon < capacity || graphite < capacity )\n"
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
                        ""
                        + "level = nucleus1.resource\n"
                        + "print(level)\n"
                        + "building.enabled = level < capacity"
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
                        ""
                        + "// Source\n"
                        + "HEAPPTR = cell3\n"
                        + "allocate heap in HEAPPTR\n"
                        + "$a = 1\n"
                        + "$b = 2\n"
                        + "$c = 3\n"
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
                        ""
                        + "HEAPPTR = cell3\n"
                        + "allocate heap in HEAPPTR\n"
                        + "print($a)\n"
                        + "$a = 1\n"
                        + "$b = 2\n"
                        + "$c = 3\n"
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
                (Seq) translateToAst(""
                        + "desired = @dagger\n"
                        + "boosting = false\n"
                        + "payTake(desired)\n"
                        + "payDrop()\n"
                        + "boost(boosting)\n"
                        + //                        "pathfind()\n" +    // pathfind no longer supported in V7
                        "idle()\n"
                        + "stop()"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "desired", "@dagger"),
                        createInstruction(SET, "boosting", "false"),
                        createInstruction(UCONTROL, "payTake", "desired"),
                        createInstruction(UCONTROL, "payDrop"),
                        createInstruction(UCONTROL, "boost", "boosting"),
                        //                        createInstruction(UCONTROL, "pathfind"),    // pathfind no longer supported in V7
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
                (Seq) translateToAst(""
                        + "x = 1\n"
                        + "print(\"\\nx: \", x)\n"
                        + "print(\"\\nx+x: \", x+x)")
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
                    (Seq) translateToAst(""
                            + "// move previous values left\n"
                            + "for n in 0 ... 40\n"
                            + "  cell1[n] = cell1[n + 1]\n"
                            + "end\n"
                            + "\n"
                            + "// delay by 1/2 a sec (0.5 s)\n"
                            + "// this depends on your framerate -- if less than 60 fps,\n"
                            + "// the delay will be longer than 0.5s\n"
                            + "deadline = @tick + 30\n"
                            + "while @tick < deadline\n"
                            + "  n += 1\n"
                            + "end\n"
                            + "\n"
                            + "// calculate the new value -- the rightmost one\n"
                            + "// change this line to graph another level\n"
                            + "cell1[39] = tank1.cryofluid / tank1.liquidCapacity\n"
                            + "\n"
                            + "// draw the graph\n"
                            + "\n"
                            + "// clear the display\n"
                            + "clear(0, 0, 0)\n"
                            + "\n"
                            + "// set the foreground color to cryofluid\n"
                            + "color(62, 207, 240, 255)\n"
                            + "\n"
                            + "// draw the bar graph\n"
                            + "for n in 0 ... 40\n"
                            + "  rect(2 * n, 0, 2, 80 * cell1[n])\n"
                            + "end\n"
                            + "\n"
                            + "drawflush(display1)")
            );
        });
    }

    @Test
    void regressionTest6() {
        // https://github.com/francois/mindcode/issues/32
        final List<LogicInstruction> result = generateAndOptimize(
                (Seq) translateToAst(""
                        + "TestVar = 0xf\n"
                        + "Result = ~TestVar\n"
                        + "print(TestVar, \"\\n\", Result)\n"
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
