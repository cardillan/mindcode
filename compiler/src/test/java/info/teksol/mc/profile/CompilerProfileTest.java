package info.teksol.mc.profile;

import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
class CompilerProfileTest {

    @Test
    void testEncodeDecode() {
        String encoded = CompilerProfile.forOptimizations(false, OptimizationLevel.BASIC)
                .setGoal(GenerationGoal.SPEED)
                .setOptimizationLevel(Optimization.CASE_EXPRESSION_OPTIMIZATION, OptimizationLevel.EXPERIMENTAL)
                .encode();

        CompilerProfile profile = CompilerProfile.noOptimizations(false).decode(encoded);

        assertEquals(GenerationGoal.SPEED, profile.getGoal());
        for (Optimization optimization : Optimization.LIST) {
            assertEquals(optimization == Optimization.CASE_EXPRESSION_OPTIMIZATION ? OptimizationLevel.EXPERIMENTAL : OptimizationLevel.BASIC,
                    profile.getOptimizationLevel(optimization),
                    "Error decoding optimization level of " + optimization);
        }
    }
}