package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class OptimizedLogicInstructionGeneratorTest extends AbstractGeneratorTest {
    @Test
    void correctlyOptimizesFunctionCallAndReturn() throws IOException {
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
                        new LogicInstruction("set", var(2), "4"),
                        new LogicInstruction("read", var(3), "cell1", "48"),
                        new LogicInstruction("op", "sub", var(7), var(3), "1"),
                        new LogicInstruction("write", var(1001), "cell1", var(7)),
                        new LogicInstruction("write", var(3), "cell1", "48"),
                        new LogicInstruction("read", var(9), "cell1", "48"),
                        new LogicInstruction("op", "sub", var(13), var(9), "1"),
                        new LogicInstruction("write", var(2), "cell1", var(13)),
                        new LogicInstruction("write", var(9), "cell1", "48"),
                        new LogicInstruction("set", "@counter", var(1000)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("read", var(16), "cell1", "48"),
                        new LogicInstruction("read", var(15), "cell1", var(16)),
                        new LogicInstruction("op", "add", var(21), var(16), "1"),
                        new LogicInstruction("write", var(21), "cell1", "48"),
                        new LogicInstruction("set", var(23), "5"),
                        new LogicInstruction("read", var(24), "cell1", "48"),
                        new LogicInstruction("op", "sub", var(28), var(24), "1"),
                        new LogicInstruction("write", var(1002), "cell1", var(28)),
                        new LogicInstruction("write", var(24), "cell1", "48"),
                        new LogicInstruction("read", var(30), "cell1", "48"),
                        new LogicInstruction("op", "sub", var(34), var(30), "1"),
                        new LogicInstruction("write", var(23), "cell1", var(34)),
                        new LogicInstruction("write", var(30), "cell1", "48"),
                        new LogicInstruction("set", "@counter", var(1000)),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("read", var(37), "cell1", "48"),
                        new LogicInstruction("read", var(36), "cell1", var(37)),
                        new LogicInstruction("op", "add", var(42), var(37), "1"),
                        new LogicInstruction("write", var(42), "cell1", "48"),
                        new LogicInstruction("op", "add", var(44), var(15), var(36)),
                        new LogicInstruction("write", var(44), "cell2", "3"),
                        new LogicInstruction("read", var(47), "cell2", "3"),
                        new LogicInstruction("op", "add", var(49), var(47), "1"),
                        new LogicInstruction("write", var(49), "cell2", "4"),
                        new LogicInstruction("end"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("read", var(52), "cell1", "48"),
                        new LogicInstruction("read", var(51), "cell1", var(52)),
                        new LogicInstruction("op", "add", var(57), var(52), "1"),
                        new LogicInstruction("write", var(57), "cell1", "48"),
                        new LogicInstruction("set", "n", var(51)),
                        new LogicInstruction("op", "mul", var(60), "2", "n"),
                        new LogicInstruction("read", var(62), "cell1", "48"),
                        new LogicInstruction("read", var(61), "cell1", var(62)),
                        new LogicInstruction("op", "add", var(67), var(62), "1"),
                        new LogicInstruction("write", var(67), "cell1", "48"),
                        new LogicInstruction("read", var(69), "cell1", "48"),
                        new LogicInstruction("op", "sub", var(73), var(69), "1"),
                        new LogicInstruction("write", var(60), "cell1", var(73)),
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
                        new LogicInstruction("set", var(3), "40"),
                        new LogicInstruction("ucontrol", "move", var(3), "pos"),
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
}