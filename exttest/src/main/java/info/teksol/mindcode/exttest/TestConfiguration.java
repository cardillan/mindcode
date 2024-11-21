package info.teksol.mindcode.exttest;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.exttest.cases.TestCaseSelector;
import info.teksol.mindcode.exttest.cases.TestCaseSelectorFull;
import info.teksol.mindcode.exttest.cases.TestCaseSelectorSampled;
import info.teksol.mindcode.v3.InputFiles;

import java.util.List;
import java.util.Map;

public class TestConfiguration {
    private final int parallelism;
    private final InputFiles inputFiles;
    private final Map<Optimization, List<OptimizationLevel>> optimizationLevels;
    private final int sampleCount;
    private final int totalCases;

    private final TestCaseSelector testCaseSelector;

    public TestConfiguration(
            InputFiles inputFiles,
            int parallelism,
            Map<Optimization, List<OptimizationLevel>> optimizationLevels,
            int sampleCount) {
        this.parallelism = parallelism;
        this.inputFiles = inputFiles;
        this.optimizationLevels = optimizationLevels;
        this.sampleCount = sampleCount;
        this.totalCases = optimizationLevels.values().stream().mapToInt(List::size)
                .reduce(1, TestConfiguration::product);

        this.testCaseSelector = sampleCount < 0 || sampleCount >= totalCases
                ? new TestCaseSelectorFull(totalCases)
                : new TestCaseSelectorSampled(totalCases, sampleCount);
    }

    private static int product(int x, int y) {
        return x * y;
    }

    public InputFiles getInputFiles() {
        return inputFiles;
    }

    public int getParallelism() {
        return parallelism;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public Map<Optimization, List<OptimizationLevel>> getOptimizationLevels() {
        return optimizationLevels;
    }

    public TestCaseSelector getTestCaseSelector() {
        return testCaseSelector;
    }

    public void setupProfile(CompilerProfile profile, int testCase) {
        int remainder = testCase;
        for (Optimization optimization : Optimization.LIST) {
            List<OptimizationLevel> levels = optimizationLevels.get(optimization);
            int index = remainder % levels.size();
            profile.setOptimizationLevel(optimization, levels.get(index));
            remainder /= levels.size();
        }

        if (remainder != 0) {
            throw new IllegalArgumentException("Invalid test case number " + testCase);
        }
    }
}
