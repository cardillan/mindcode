package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.AbstractAstTest;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.MOpcode;
import info.teksol.mindcode.mindustry.MOpcodeGenerator;
import info.teksol.mindcode.mindustry.MOpcodeLabelResolver;
import info.teksol.mindcode.mindustry.MOpcodePeepholeOptimizer;
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
