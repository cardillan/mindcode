package info.teksol.mindcode.compiler;

import info.teksol.mindcode.ast.Seq;
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
                        getInstructionProcessor(),
                        generateUnoptimized((Seq) translateToAst("while true n = n + 1 end"))
                )
        );
    }

    @Test
    void resolvesVirtualInstructions() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, "11", "always"),
                        createInstruction(WRITE, "10", "cell1", "__sp"),
                        createInstruction(OP, "add", "__sp", "__sp", "1"),
                        createInstruction(OP, "sub", "__sp", "__sp", "1"),
                        createInstruction(READ, "x", "cell1", "__sp"),
                        createInstruction(WRITE, "11", "cell1", "__sp"),
                        createInstruction(OP, "add", "__sp", "__sp", "1"),
                        createInstruction(SET, "@counter", "8"),
                        createInstruction(OP, "sub", "__sp", "__sp", "1"),
                        createInstruction(READ, "__tmp0", "cell1", "__sp"),
                        createInstruction(SET, "@counter", "__tmp0"),
                        createInstruction(END)
                ),
                LogicInstructionLabelResolver.resolve(
                        getInstructionProcessor(),
                        List.of(
                                createInstruction(JUMP, "finish", "always"),
                                createInstruction(PUSH, "cell1", "10"),
                                createInstruction(POP, "cell1", "x"),
                                createInstruction(CALL, "cell1", "callAddr", "returnAddr"),
                                createInstruction(LABEL, "callAddr"),
                                createInstruction(RETURN, "cell1"),
                                createInstruction(LABEL, "returnAddr"),
                                createInstruction(LABEL, "finish"),
                                createInstruction(END)
                        )
                )
        );
    }


}
