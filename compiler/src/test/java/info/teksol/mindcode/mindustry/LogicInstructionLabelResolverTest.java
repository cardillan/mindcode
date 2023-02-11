package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.generator.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

class LogicInstructionLabelResolverTest extends AbstractGeneratorTest {
    @Test
    void resolvesAbsoluteAddressesOfLabels() {
        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(JUMP, "5", "equal", "true", "false"),
                        new LogicInstruction(SET, var(0), "1"),
                        new LogicInstruction(OP, "add", var(1), "n", var(0)),
                        new LogicInstruction(SET, "n", var(1)),
                        new LogicInstruction(JUMP, "0", "always"),
                        new LogicInstruction(END)
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
                        new LogicInstruction(SET, var(0), "63"),
                        new LogicInstruction(SET, var(1), "63"),
                        new LogicInstruction(WRITE, var(0), "cell1", var(1)),
                        new LogicInstruction(SET, var(3), "63"),
                        new LogicInstruction(READ, var(4), "cell1", var(3)),
                        new LogicInstruction(SET, var(2), var(4)),
                        new LogicInstruction(SET, var(5), "1"),
                        new LogicInstruction(OP, "sub", var(6), var(2), var(5)),
                        new LogicInstruction(SET, var(2), var(6)),
                        new LogicInstruction(WRITE, "13", "cell1", var(2)),
                        new LogicInstruction(SET, var(7), "63"),
                        new LogicInstruction(WRITE, var(2), "cell1", var(7)),
                        new LogicInstruction(SET, "@counter", "24"),
                        new LogicInstruction(SET, var(10), "63"),
                        new LogicInstruction(READ, var(11), "cell1", var(10)),
                        new LogicInstruction(SET, var(9), var(11)),
                        new LogicInstruction(READ, var(12), "cell1", var(9)),
                        new LogicInstruction(SET, var(8), var(12)),
                        new LogicInstruction(SET, var(13), "1"),
                        new LogicInstruction(OP, "add", var(14), var(9), var(13)),
                        new LogicInstruction(SET, var(9), var(14)),
                        new LogicInstruction(SET, var(15), "63"),
                        new LogicInstruction(WRITE, var(9), "cell1", var(15)),
                        new LogicInstruction(END),
                        new LogicInstruction(SET, var(16), "1"),
                        new LogicInstruction(SET, var(19), "63"),
                        new LogicInstruction(READ, var(20), "cell1", var(19)),
                        new LogicInstruction(SET, var(18), var(20)),
                        new LogicInstruction(READ, var(21), "cell1", var(18)),
                        new LogicInstruction(SET, var(17), var(21)),
                        new LogicInstruction(SET, var(22), "1"),
                        new LogicInstruction(OP, "add", var(23), var(18), var(22)),
                        new LogicInstruction(SET, var(18), var(23)),
                        new LogicInstruction(SET, var(24), "63"),
                        new LogicInstruction(WRITE, var(18), "cell1", var(24)),
                        new LogicInstruction(SET, var(26), "63"),
                        new LogicInstruction(READ, var(27), "cell1", var(26)),
                        new LogicInstruction(SET, var(25), var(27)),
                        new LogicInstruction(SET, var(28), "1"),
                        new LogicInstruction(OP, "sub", var(29), var(25), var(28)),
                        new LogicInstruction(SET, var(25), var(29)),
                        new LogicInstruction(WRITE, var(16), "cell1", var(25)),
                        new LogicInstruction(SET, var(30), "63"),
                        new LogicInstruction(WRITE, var(25), "cell1", var(30)),
                        new LogicInstruction(SET, "@counter", var(17)),
                        new LogicInstruction(END)
                ),
                LogicInstructionLabelResolver.resolve(
                        LogicInstructionGenerator.generateUnoptimized(
                                (Seq) translateToAst("allocate stack in cell1\n\ndef bar\n1\nend\n\nbar()")
                        )
                )
        );
    }
}
