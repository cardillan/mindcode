package info.teksol.mindcode.compiler;

import info.teksol.mindcode.logic.Condition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class LogicInstructionLabelResolverTest extends AbstractGeneratorTest {
    @Test
    void resolvesAbsoluteAddressesOfLabels() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, "4", "equal", "true", "false"),
                        createInstruction(OP, "add", var(0), "n", "1"),
                        createInstruction(SET, "n", var(0)),
                        createInstruction(JUMP, "0", "always"),
                        createInstruction(END)
                ),
                LogicInstructionLabelResolver.resolve(
                        instructionProcessor,
                        generateInstructions("while true n = n + 1 end").instructions()
                )
        );
    }

    @Test
    void resolvesVirtualInstructions() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, "11", "always"),
                        createInstruction(WRITE, "a", "cell1", "__sp"),
                        createInstruction(OP, "add", "__sp", "__sp", "1"),
                        createInstruction(OP, "sub", "__sp", "__sp", "1"),
                        createInstruction(READ, "a", "cell1", "__sp"),
                        createInstruction(WRITE, "11", "cell1", "__sp"),
                        createInstruction(OP, "add", "__sp", "__sp", "1"),
                        createInstruction(SET, "@counter", "8"),
                        createInstruction(OP, "sub", "__sp", "__sp", "1"),
                        createInstruction(READ, "__tmp0", "cell1", "__sp"),
                        createInstruction(SET, "@counter", "__tmp0"),
                        createInstruction(END)
                ),
                LogicInstructionLabelResolver.resolve(
                        instructionProcessor,
                        List.of(
                                createInstruction(JUMP, label0, Condition.ALWAYS),
                                createInstruction(PUSH, cell1, a),
                                createInstruction(POP, cell1, a),
                                createInstruction(CALLREC, cell1, label1, label2),
                                createInstruction(LABEL, label1),
                                createInstruction(RETURN, cell1),
                                createInstruction(LABEL, label2),
                                createInstruction(LABEL, label0),
                                createInstruction(END)
                        )
                )
        );
    }
}
