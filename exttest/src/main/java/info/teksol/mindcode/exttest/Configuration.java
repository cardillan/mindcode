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

public record Configuration(
        int threads,
        String outputPath,
        boolean fullTests,
        int sampleMultiplier,
        List<TestConfiguration> configurations) {

    public void addTestConfiguration(
            String sourceFileName,
            InputFiles inputFiles,
            Map<Optimization, List<OptimizationLevel>> optimizationLevels,
            List<GenerationGoal> generationGoals,
            int sampleCount,
            int failureLimit,
            boolean run) {

        configurations.add(new TestConfiguration(
                sourceFileName,
                inputFiles,
                optimizationLevels,
                generationGoals,
                sampleCount * sampleMultiplier,
                failureLimit,
                run));
    }

    public class TestConfiguration {
        private final String sourceFileName;
        private final InputFiles inputFiles;
        private final Path resultPath;
        private final Map<Optimization, List<OptimizationLevel>> optimizationLevels;
        private final List<GenerationGoal> generationGoals;
        private final int sampleCount;
        private final int totalCases;
        private final int failureLimit;
        private final boolean run;

        private final TestCaseSelector testCaseSelector;

        public TestConfiguration(
                String sourceFileName,
                InputFiles inputFiles,
                Map<Optimization, List<OptimizationLevel>> optimizationLevels,
                List<GenerationGoal> generationGoals,
                int sampleCount,
                int failureLimit,
                boolean run) {
            this.sourceFileName = sourceFileName;
            this.inputFiles = inputFiles;
            this.optimizationLevels = optimizationLevels;
            this.generationGoals = generationGoals;
            this.sampleCount = sampleCount;
            this.failureLimit = failureLimit;
            this.run = run;

            this.totalCases = optimizationLevels.values().stream().mapToInt(List::size)
                                      .reduce(1, TestConfiguration::product) * generationGoals.size();

            this.testCaseSelector = fullTests || sampleCount >= totalCases
                    ? new TestCaseSelectorFull(totalCases)
                    : new TestCaseSelectorSampled(totalCases, sampleCount);

            Path sourcePath = Path.of(sourceFileName);
            String resultsName = "results-" + sourcePath.getFileName().toString().replace(".mnd", "") + ".txt.";
            if (outputPath.isEmpty()) {
                Path parentPath = sourcePath.getParent();
                resultPath = Path.of(parentPath == null ? "" : parentPath.toString(), resultsName);
            } else {
                resultPath = Path.of(outputPath, resultsName);
            }
        }

        public Configuration global() {
            return Configuration.this;
        }

        private static int product(int x, int y) {
            return x * y;
        }

        public String getSourceFileName() {
            return sourceFileName;
        }

        public boolean isRun() {
            return run;
        }

        public InputFiles getInputFiles() {
            return inputFiles;
        }

        public Path getResultPath() {
            return resultPath;
        }

        public int getSampleCount() {
            return sampleCount;
        }

        public int getTotalCases() {
            return totalCases;
        }

        public int getFailureLimit() {
            return failureLimit;
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

        public CompilerProfile createCompilerProfile(int testCase) {
            CompilerProfile profile = new CompilerProfile(false)
                    .setAllOptimizationLevels(OptimizationLevel.NONE)
                    .setOptimizationPasses(50)
                    .setRun(run);

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

            return profile;
        }
    }
}
