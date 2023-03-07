package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

class LogicInstructionLabelResolverTest extends AbstractGeneratorTest {
    @Test
    void resolvesAbsoluteAddressesOfLabels() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("jump", "5", "equal", "true", "false"),
                        new LogicInstruction("set", var(0), "1"),
                        new LogicInstruction("op", "add", var(1), "n", var(0)),
                        new LogicInstruction("set", "n", var(1)),
                        new LogicInstruction("jump", "0", "always"),
                        new LogicInstruction("end")
                ),
                LogicInstructionLabelResolver.resolve(
                        LogicInstructionGenerator.generateUnoptimized(
                                (Seq) translateToAst("while true\nn = n + 1\nend\n")
                        )
                )
        );
    }

    @Test
    void resolvesFunctionCallsAndReturns() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", var(0), "63"),
                        new LogicInstruction("set", var(1), "63"),
                        new LogicInstruction("write", var(0), "cell1", var(1)),
                        new LogicInstruction("set", var(3), "63"),
                        new LogicInstruction("read", var(4), "cell1", var(3)),
                        new LogicInstruction("set", var(2), var(4)),
                        new LogicInstruction("set", var(5), "1"),
                        new LogicInstruction("op", "sub", var(6), var(2), var(5)),
                        new LogicInstruction("set", var(2), var(6)),
                        new LogicInstruction("write", "13", "cell1", var(2)),
                        new LogicInstruction("set", var(7), "63"),
                        new LogicInstruction("write", var(2), "cell1", var(7)),
                        new LogicInstruction("set", "@counter", "24"),
                        new LogicInstruction("set", var(10), "63"),
                        new LogicInstruction("read", var(11), "cell1", var(10)),
                        new LogicInstruction("set", var(9), var(11)),
                        new LogicInstruction("read", var(12), "cell1", var(9)),
                        new LogicInstruction("set", var(8), var(12)),
                        new LogicInstruction("set", var(13), "1"),
                        new LogicInstruction("op", "add", var(14), var(9), var(13)),
                        new LogicInstruction("set", var(9), var(14)),
                        new LogicInstruction("set", var(15), "63"),
                        new LogicInstruction("write", var(9), "cell1", var(15)),
                        new LogicInstruction("end"),
                        new LogicInstruction("set", var(16), "1"),
                        new LogicInstruction("set", var(19), "63"),
                        new LogicInstruction("read", var(20), "cell1", var(19)),
                        new LogicInstruction("set", var(18), var(20)),
                        new LogicInstruction("read", var(21), "cell1", var(18)),
                        new LogicInstruction("set", var(17), var(21)),
                        new LogicInstruction("set", var(22), "1"),
                        new LogicInstruction("op", "add", var(23), var(18), var(22)),
                        new LogicInstruction("set", var(18), var(23)),
                        new LogicInstruction("set", var(24), "63"),
                        new LogicInstruction("write", var(18), "cell1", var(24)),
                        new LogicInstruction("set", var(26), "63"),
                        new LogicInstruction("read", var(27), "cell1", var(26)),
                        new LogicInstruction("set", var(25), var(27)),
                        new LogicInstruction("set", var(28), "1"),
                        new LogicInstruction("op", "sub", var(29), var(25), var(28)),
                        new LogicInstruction("set", var(25), var(29)),
                        new LogicInstruction("write", var(16), "cell1", var(25)),
                        new LogicInstruction("set", var(30), "63"),
                        new LogicInstruction("write", var(25), "cell1", var(30)),
                        new LogicInstruction("set", "@counter", var(17)),
                        new LogicInstruction("end")
                ),
                LogicInstructionLabelResolver.resolve(
                        LogicInstructionGenerator.generateUnoptimized(
                                (Seq) translateToAst("allocate stack in cell1\n\ndef bar\n1\nend\n\nbar()")
                        )
                )
        );
    }
}
