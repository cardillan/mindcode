package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogicInstructionLabelResolverTest extends AbstractGeneratorTest {
    @Test
    void resolvesAbsoluteAddressesOfLabels() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("jump", "5", "notEqual", "true", "true"),
                                new LogicInstruction("set", "tmp0", "1"),
                                new LogicInstruction("op", "add", "tmp1", "n", "tmp0"),
                                new LogicInstruction("set", "n", "tmp1"),
                                new LogicInstruction("jump", "0", "always"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionGenerator.generateFrom(
                                        (Seq) translateToAst("while true\nn = n + 1\nend\n")
                                )
                        )
                )
        );
    }

    @Test
    void resolvesFunctionCallsAndReturns() {
        assertLogicInstructionListMatches(
                List.of(
                        new LogicInstruction("set", match(0), "63"),
                        new LogicInstruction("set", match(1), "63"),
                        new LogicInstruction("write", match(0), "cell1", match(1)),
                        new LogicInstruction("set", match(3), "63"),
                        new LogicInstruction("read", match(4), "cell1", match(3)),
                        new LogicInstruction("set", match(2), match(4)),
                        new LogicInstruction("set", match(5), "1"),
                        new LogicInstruction("op", "sub", match(6), match(2), match(5)),
                        new LogicInstruction("set", match(2), match(6)),
                        new LogicInstruction("write", "13", "cell1", match(2)),
                        new LogicInstruction("set", match(7), "63"),
                        new LogicInstruction("write", match(2), "cell1", match(7)),
                        new LogicInstruction("set", "@counter", "24"),
                        new LogicInstruction("set", match(10), "63"),
                        new LogicInstruction("read", match(11), "cell1", match(10)),
                        new LogicInstruction("set", match(9), match(11)),
                        new LogicInstruction("read", match(12), "cell1", match(9)),
                        new LogicInstruction("set", match(8), match(12)),
                        new LogicInstruction("set", match(13), "1"),
                        new LogicInstruction("op", "add", match(14), match(9), match(13)),
                        new LogicInstruction("set", match(9), match(14)),
                        new LogicInstruction("set", match(15), "63"),
                        new LogicInstruction("write", match(9), "cell1", match(15)),
                        new LogicInstruction("end"),
                        new LogicInstruction("set", match(16), "1"),
                        new LogicInstruction("set", match(19), "63"),
                        new LogicInstruction("read", match(20), "cell1", match(19)),
                        new LogicInstruction("set", match(18), match(20)),
                        new LogicInstruction("read", match(21), "cell1", match(18)),
                        new LogicInstruction("set", match(17), match(21)),
                        new LogicInstruction("set", match(22), "1"),
                        new LogicInstruction("op", "add", match(23), match(18), match(22)),
                        new LogicInstruction("set", match(18), match(23)),
                        new LogicInstruction("set", match(24), "63"),
                        new LogicInstruction("write", match(18), "cell1", match(24)),
                        new LogicInstruction("set", match(26), "63"),
                        new LogicInstruction("read", match(27), "cell1", match(26)),
                        new LogicInstruction("set", match(25), match(27)),
                        new LogicInstruction("set", match(28), "1"),
                        new LogicInstruction("op", "sub", match(29), match(25), match(28)),
                        new LogicInstruction("set", match(25), match(29)),
                        new LogicInstruction("write", match(16), "cell1", match(25)),
                        new LogicInstruction("set", match(30), "63"),
                        new LogicInstruction("write", match(25), "cell1", match(30)),
                        new LogicInstruction("set", "@counter", match(17)),
                        new LogicInstruction("end")
                ),
                LogicInstructionLabelResolver.resolve(
                        LogicInstructionGenerator.generateFrom(
                                (Seq) translateToAst("allocate stack in cell1\n\ndef bar\n1\nend\n\nbar()")
                        )
                )
        );
    }
}
