package info.teksol.mindcode.exttest;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.exttest.cases.TestCaseSelector;
import info.teksol.mindcode.exttest.cases.TestCaseSelectorFull;
import info.teksol.mindcode.exttest.cases.TestCaseSelectorSampled;
import info.teksol.mindcode.v3.InputFiles;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class TestConfiguration {
    private final int parallelism;
    private final InputFiles inputFiles;
    private final Path resultPath;
    private final Map<Optimization, List<OptimizationLevel>> optimizationLevels;
    private final List<GenerationGoal> generationGoals;
    private final int sampleCount;
    private final int totalCases;
    private final boolean run;

    private final TestCaseSelector testCaseSelector;

    public TestConfiguration(
            String sourceFileName,
            InputFiles inputFiles,
            int parallelism,
            Map<Optimization, List<OptimizationLevel>> optimizationLevels,
            List<GenerationGoal> generationGoals,
            int sampleCount,
            boolean run) {
        this.parallelism = parallelism;
        this.inputFiles = inputFiles;
        this.optimizationLevels = optimizationLevels;
        this.generationGoals = generationGoals;
        this.sampleCount = sampleCount;
        this.run = run;

        this.totalCases = optimizationLevels.values().stream().mapToInt(List::size)
                .reduce(1, TestConfiguration::product) * generationGoals.size();

        this.testCaseSelector = sampleCount < 0 || sampleCount >= totalCases
                ? new TestCaseSelectorFull(totalCases)
                : new TestCaseSelectorSampled(totalCases, sampleCount);

        Path sourcePath = Path.of(sourceFileName);
        Path parentPath = sourcePath.getParent();
        String resultsName = "results-" + sourcePath.getFileName().toString().replace(".mnd", "") + ".txt.";
        resultPath = Path.of(parentPath == null ? "" : parentPath.toString(), resultsName);
    }

    private static int product(int x, int y) {
        return x * y;
    }

    public InputFiles getInputFiles() {
        return inputFiles;
    }

    public Path getResultPath() {
        return resultPath;
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

    public List<GenerationGoal> getGenerationGoals() {
        return generationGoals;
    }

    public TestCaseSelector getTestCaseSelector() {
        return testCaseSelector;
    }

    public void setupProfile(CompilerProfile profile, int testCase) {
        profile.setRun(run);

        // Goal first
        int goal = testCase % generationGoals.size();
        profile.setGoal(generationGoals.get(goal));

        int remainder = testCase / generationGoals.size();
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
