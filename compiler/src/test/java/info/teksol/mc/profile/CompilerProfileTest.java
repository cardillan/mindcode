package info.teksol.mc.profile;

import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerProfileTest {

    @Test
    void testEncodeDecode() {
        String encoded = new CompilerProfile(false)
                .setAllOptimizationLevels(OptimizationLevel.BASIC)
                .setGoal(GenerationGoal.SPEED)
                .setOptimizationLevel(Optimization.CASE_EXPRESSION_OPTIMIZATION, OptimizationLevel.ADVANCED)
                .encode();

        CompilerProfile profile = new CompilerProfile(false).decode(encoded);

        assertEquals(GenerationGoal.SPEED, profile.getGoal());
        for (Optimization optimization : Optimization.LIST) {
            assertEquals(optimization == Optimization.CASE_EXPRESSION_OPTIMIZATION ? OptimizationLevel.ADVANCED : OptimizationLevel.BASIC,
                    profile.getOptimizationLevel(optimization),
                    "Error decoding optimization level of " + optimization);
        }
    }
}