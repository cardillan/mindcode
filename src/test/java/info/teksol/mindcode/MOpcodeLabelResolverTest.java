package info.teksol.mindcode;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MOpcodeLabelResolverTest extends AbstractAstTest {
    @Test
    void resolvesAbsoluteAddressesOfLabels() {
        assertEquals(
                List.of(
                        new MOpcode("jump", "3", "notEqual", "true", "true"),
                        new MOpcode("op", "add", "n", "n", "1"),
                        new MOpcode("jump", "0", "always"),
                        new MOpcode("end")
                ),
                MOpcodeLabelResolver.resolve(
                        MOpcodePeepholeOptimizer.optimize(
                                MOpcodeGenerator.generateFrom(
                                        (Seq) translateToAst("while true {\nn = n + 1\n}\n")
                                )
                        )
                )
        );
    }
}
