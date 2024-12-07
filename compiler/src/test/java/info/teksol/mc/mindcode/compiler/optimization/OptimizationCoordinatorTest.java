package info.teksol.mc.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class OptimizationCoordinatorTest {

    @Test
    void debugFacilitiesAreInactive() {
        assertFalse(OptimizationCoordinator.TRACE, "TRACE is active");
        assertFalse(OptimizationCoordinator.DEBUG_PRINT, "DEBUG_PRINT is active");
        assertFalse(OptimizationCoordinator.IGNORE_UNINITIALIZED, "IGNORE_UNINITIALIZED is active");
    }

}