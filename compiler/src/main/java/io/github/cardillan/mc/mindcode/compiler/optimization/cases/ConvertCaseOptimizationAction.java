package io.github.cardillan.mc.mindcode.compiler.optimization.cases;

import io.github.cardillan.mc.mindcode.compiler.optimization.OptimizationAction;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ConvertCaseOptimizationAction extends OptimizationAction {
    int getId();

    int rawCost();

    int originalSteps();

    int executionSteps();

    boolean applied();
}
