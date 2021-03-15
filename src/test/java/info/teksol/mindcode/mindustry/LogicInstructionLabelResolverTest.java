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
                                        (Seq) translateToAst("while true {\nn = n + 1\n}\n")
                                )
                        )
                )
        );
    }
}
