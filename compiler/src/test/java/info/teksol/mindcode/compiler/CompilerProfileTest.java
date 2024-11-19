package info.teksol.mindcode.compiler;

import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerProfileTest {

    @Test
    void testEncodeDecode() {
        String encoded = new CompilerProfile(false)
                .setAllOptimizationLevels(OptimizationLevel.BASIC)
                .setOptimizationLevel(Optimization.CASE_EXPRESSION_OPTIMIZATION, OptimizationLevel.ADVANCED)
                .encode();

        CompilerProfile profile = new CompilerProfile(false).decode(encoded);

        for (Optimization optimization : Optimization.LIST) {
            assertEquals(optimization == Optimization.CASE_EXPRESSION_OPTIMIZATION ? OptimizationLevel.ADVANCED : OptimizationLevel.BASIC,
                    profile.getOptimizationLevel(optimization),
                    "Error decoding optimization level of " + optimization);
        }
    }
}