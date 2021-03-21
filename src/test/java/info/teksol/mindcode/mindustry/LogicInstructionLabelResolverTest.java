package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogicInstructionLabelResolverTest extends AbstractAstTest {
    @Test
    void resolvesAbsoluteAddressesOfLabels() {
        assertEquals(
                List.of(
                        new LogicInstruction("jump", "3", "notEqual", "true", "true"),
                        new LogicInstruction("op", "add", "n", "n", "1"),
                        new LogicInstruction("jump", "0", "always"),
                        new LogicInstruction("end")
                ),
                LogicInstructionLabelResolver.resolve(
                        LogicInstructionPeepholeOptimizer.optimize(
                                LogicInstructionGenerator.generateFrom(
                                        (Seq) translateToAst("while true\nn = n + 1\nend\n")
                                )
                        )
                )
        );
    }

    @Test
    void resolvesFunctionCallsAndReturns() {
        assertEquals(
                prettyPrint(
                        List.of(
                                new LogicInstruction("write", "63", "cell1", "63"),
                                new LogicInstruction("read", "tmp6", "cell1", "63"),
                                new LogicInstruction("op", "sub", "tmp6", "tmp6", "1"),
                                new LogicInstruction("write", "6", "cell1", "tmp6"),
                                new LogicInstruction("write", "tmp6", "cell1", "63"),
                                new LogicInstruction("set", "@counter", "12"),
                                new LogicInstruction("read", "tmp16", "cell1", "63"), // label1
                                new LogicInstruction("read", "tmp17", "cell1", "tmp16"),
                                new LogicInstruction("set", "tmp13", "tmp17"),
                                new LogicInstruction("op", "add", "tmp16", "tmp16", "1"),
                                new LogicInstruction("write", "tmp16", "cell1", "63"),
                                new LogicInstruction("end"),
                                new LogicInstruction("set", "tmp23", "1"), // label0
                                new LogicInstruction("read", "tmp27", "cell1", "63"),
                                new LogicInstruction("read", "tmp28", "cell1", "tmp27"),
                                new LogicInstruction("set", "tmp24", "tmp28"),
                                new LogicInstruction("op", "add", "tmp27", "tmp27", "1"),
                                new LogicInstruction("write", "tmp27", "cell1", "63"),
                                new LogicInstruction("read", "tmp36", "cell1", "63"),
                                new LogicInstruction("op", "sub", "tmp36", "tmp36", "1"),
                                new LogicInstruction("write", "tmp23", "cell1", "tmp36"),
                                new LogicInstruction("write", "tmp36", "cell1", "63"),
                                new LogicInstruction("set", "@counter", "tmp24"),
                                new LogicInstruction("end")
                        )
                ),
                prettyPrint(
                        LogicInstructionLabelResolver.resolve(
                                LogicInstructionPeepholeOptimizer.optimize(
                                        LogicInstructionGenerator.generateFrom(
                                                (Seq) translateToAst("allocate stack in cell1\n\ndef bar\n1\nend\n\nbar()")
                                        )
                                )
                        )
                )
        );
    }
}
