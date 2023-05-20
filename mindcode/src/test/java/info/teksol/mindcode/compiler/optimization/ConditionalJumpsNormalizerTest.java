package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class ConditionalJumpsNormalizerTest extends AbstractOptimizerTest<ConditionalJumpsNormalizer> {

    @Override
    protected Class<ConditionalJumpsNormalizer> getTestedClass() {
        return ConditionalJumpsNormalizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(Optimization.CONDITIONAL_JUMPS_NORMALIZATION);
    }

    @Test
    void normalizesConditionalJump() {
        assertCompilesTo("""
                        while false
                            print("Here")
                        end
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "always"),
                createInstruction(PRINT, "\"Here\""),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void removesAlwaysFalseJump() {
        assertCompilesTo("""
                        while true
                            1
                        end
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }
}
