package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

@Order(99)
class ConditionalJumpsNormalizerTest extends AbstractOptimizerTest<ConditionalJumpsNormalizer> {

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
                        while false do
                            print("Here");
                        end;
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "always"),
                createInstruction(PRINT, q("Here")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void removesAlwaysFalseJump() {
        assertCompilesTo("""
                        while true do
                            1;
                        end;
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(END)
        );
    }

    @Test
    void evaluatesBuiltInEqualComparison() {
        assertCompilesTo(
                createTestCompiler(createCompilerProfile().setAllOptimizationLevels(OptimizationLevel.ADVANCED)),
                        """
                        if @coal == @lead then
                            print("yes");
                        else
                            print("no");
                        end;
                        """,
                createInstruction(PRINT, q("no"))
        );
    }

    @Test
    void evaluatesBuiltInNonEqualComparison() {
        assertCompilesTo(
                createTestCompiler(createCompilerProfile().setAllOptimizationLevels(OptimizationLevel.ADVANCED)),
                        """
                        if @coal != @lead then
                            print("yes");
                        else
                            print("no");
                        end;
                        """,
                createInstruction(PRINT, q("yes"))
        );
    }

    @Test
    void evaluatesBuiltInLessThanOrEqualComparison() {
        assertCompilesTo(
                createTestCompiler(createCompilerProfile().setAllOptimizationLevels(OptimizationLevel.ADVANCED)),
                        """
                        if @coal <= @lead then
                            print("yes");
                        else
                            print("no");
                        end;
                        """,
                createInstruction(PRINT, q("yes"))
        );
    }

    @Test
    void evaluatesBuiltInLessThanComparison() {
        assertCompilesTo(
                createTestCompiler(createCompilerProfile().setAllOptimizationLevels(OptimizationLevel.ADVANCED)),
                        """
                        if @coal < @lead then
                            print("yes");
                        else
                            print("no");
                        end;
                        """,
                createInstruction(PRINT, q("no"))
        );
    }
}
