package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class OptimizationCoordinatorTest {

    @Test
    void traceIsInactive() {
        assertFalse(OptimizationCoordinator.TRACE, "TRACE is active");
        assertFalse(OptimizationCoordinator.DEBUG_PRINT, "DEBUG_PRINT is active");
        assertFalse(OptimizationCoordinator.IGNORE_UNINITIALIZED, "IGNORE_UNINITIALIZED is active");
    }

}