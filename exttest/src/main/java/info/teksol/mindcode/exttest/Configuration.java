package info.teksol.mindcode.exttest;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import info.teksol.mindcode.exttest.cases.TestCaseCreatorFull;
import info.teksol.mindcode.exttest.cases.TestCaseCreatorSampled;
import info.teksol.mindcode.v3.InputFiles;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public record Configuration(
        int threads,
        String outputPath,
        boolean fullTests,
        int sampleMultiplier,
        List<SingleTestConfiguration> configurations) {

    public void addTestConfiguration(
            String sourceFileName,
            InputFiles inputFiles,
            Map<Optimization, List<OptimizationLevel>> optimizationLevels,
            List<GenerationGoal> generationGoals,
            int sampleCount,
            int failureLimit,
            boolean run) {

        configurations.add(new SingleTestConfiguration(
                sourceFileName,
                inputFiles,
                optimizationLevels,
                generationGoals,
                sampleCount * sampleMultiplier,
                failureLimit,
                run));
    }

    private static int product(int x, int y) {
        return x * y;
    }

    public class SingleTestConfiguration implements TestConfiguration {
        private final String sourceFileName;
        private final InputFiles inputFiles;
        private final Path resultPath;
        private final Map<Optimization, List<OptimizationLevel>> optimizationLevels;
        private final List<GenerationGoal> generationGoals;
        private final int sampleCount;
        private final int totalCases;
        private final int failureLimit;
        private final boolean run;

        private final TestCaseCreator testCaseCreator;

        public SingleTestConfiguration(
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
                                      .reduce(1, Configuration::product) * generationGoals.size();

            this.testCaseCreator = fullTests || sampleCount >= totalCases
                    ? new TestCaseCreatorFull(this)
                    : new TestCaseCreatorSampled(this);

            Path sourcePath = Path.of(sourceFileName);
            String resultsName = "results-" + sourcePath.getFileName().toString().replace(".mnd", "") + ".txt.";
            if (outputPath.isEmpty()) {
                Path parentPath = sourcePath.getParent();
                resultPath = Path.of(parentPath == null ? "" : parentPath.toString(), resultsName);
            } else {
                resultPath = Path.of(outputPath, resultsName);
            }
        }

        @Override
        public int getThreads() {
            return threads;
        }

        public String getSourceFileName() {
            return sourceFileName;
        }

        public boolean isRun() {
            return run;
        }

        @Override
        public InputFiles getInputFiles() {
            return inputFiles;
        }

        public Path getResultPath() {
            return resultPath;
        }

        @Override
        public int getSampleCount() {
            return sampleCount;
        }

        @Override
        public int getTotalCases() {
            return totalCases;
        }

        @Override
        public int getFailureLimit() {
            return failureLimit;
        }

        @Override
        public Map<Optimization, List<OptimizationLevel>> getOptimizationLevels() {
            return optimizationLevels;
        }

        @Override
        public List<GenerationGoal> getGenerationGoals() {
            return generationGoals;
        }

        @Override
        public TestCaseCreator getTestCaseCreator() {
            return testCaseCreator;
        }

        @Override
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
