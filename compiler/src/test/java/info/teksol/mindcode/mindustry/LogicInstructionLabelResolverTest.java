package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

class LogicInstructionLabelResolverTest extends AbstractGeneratorTest {
    @Test
    void resolvesAbsoluteAddressesOfLabels() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, "5", "equal", "true", "false"),
                        createInstruction(SET, var(0), "1"),
                        createInstruction(OP, "add", var(1), "n", var(0)),
                        createInstruction(SET, "n", var(1)),
                        createInstruction(JUMP, "0", "always"),
                        createInstruction(END)
                ),
                LogicInstructionLabelResolver.resolve(
                        getInstructionProcessor(),
                        generateUnoptimized(
                                (Seq) translateToAst("while true\nn = n + 1\nend\n")
                        )
                )
        );
    }

    @Test
    void resolvesFunctionCallsAndReturns() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "63"),
                        createInstruction(SET, var(1), "63"),
                        createInstruction(WRITE, var(0), "cell1", var(1)),
                        createInstruction(SET, var(3), "63"),
                        createInstruction(READ, var(4), "cell1", var(3)),
                        createInstruction(SET, var(2), var(4)),
                        createInstruction(SET, var(5), "1"),
                        createInstruction(OP, "sub", var(6), var(2), var(5)),
                        createInstruction(SET, var(2), var(6)),
                        createInstruction(WRITE, "13", "cell1", var(2)),
                        createInstruction(SET, var(7), "63"),
                        createInstruction(WRITE, var(2), "cell1", var(7)),
                        createInstruction(SET, "@counter", "24"),
                        createInstruction(SET, var(10), "63"),
                        createInstruction(READ, var(11), "cell1", var(10)),
                        createInstruction(SET, var(9), var(11)),
                        createInstruction(READ, var(12), "cell1", var(9)),
                        createInstruction(SET, var(8), var(12)),
                        createInstruction(SET, var(13), "1"),
                        createInstruction(OP, "add", var(14), var(9), var(13)),
                        createInstruction(SET, var(9), var(14)),
                        createInstruction(SET, var(15), "63"),
                        createInstruction(WRITE, var(9), "cell1", var(15)),
                        createInstruction(END),
                        createInstruction(SET, var(16), "1"),
                        createInstruction(SET, var(19), "63"),
                        createInstruction(READ, var(20), "cell1", var(19)),
                        createInstruction(SET, var(18), var(20)),
                        createInstruction(READ, var(21), "cell1", var(18)),
                        createInstruction(SET, var(17), var(21)),
                        createInstruction(SET, var(22), "1"),
                        createInstruction(OP, "add", var(23), var(18), var(22)),
                        createInstruction(SET, var(18), var(23)),
                        createInstruction(SET, var(24), "63"),
                        createInstruction(WRITE, var(18), "cell1", var(24)),
                        createInstruction(SET, var(26), "63"),
                        createInstruction(READ, var(27), "cell1", var(26)),
                        createInstruction(SET, var(25), var(27)),
                        createInstruction(SET, var(28), "1"),
                        createInstruction(OP, "sub", var(29), var(25), var(28)),
                        createInstruction(SET, var(25), var(29)),
                        createInstruction(WRITE, var(16), "cell1", var(25)),
                        createInstruction(SET, var(30), "63"),
                        createInstruction(WRITE, var(25), "cell1", var(30)),
                        createInstruction(SET, "@counter", var(17)),
                        createInstruction(END)
                ),
                LogicInstructionLabelResolver.resolve(
                        getInstructionProcessor(),
                        generateUnoptimized(
                                (Seq) translateToAst("allocate stack in cell1\n\ndef bar\n1\nend\n\nbar()")
                        )
                )
        );
    }
}
