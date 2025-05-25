package info.teksol.mindcode.exttest;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import info.teksol.mindcode.exttest.cases.TestCaseCreatorFull;
import info.teksol.mindcode.exttest.cases.TestCaseCreatorSampled;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@NullMarked
public record Configuration(
        int threads,
        String outputPath,
        boolean fullTests,
        int sampleMultiplier,
        List<SingleTestConfiguration> configurations) {

    public Configuration {
        if (threads < 1) {
            throw new IllegalArgumentException("threads must be at least 1");
        }
    }

    public void addTestConfiguration(
            String sourceFileName,
            InputFiles inputFiles,
            Map<Optimization, List<OptimizationLevel>> optimizationLevels,
            List<GenerationGoal> generationGoals,
            List<Boolean> symbolicLabels,
            int sampleCount,
            int failureLimit,
            int caseSwitching,
            boolean run) {

        configurations.add(new SingleTestConfiguration(
                sourceFileName,
                inputFiles,
                optimizationLevels,
                generationGoals,
                symbolicLabels,
                sampleCount * sampleMultiplier,
                failureLimit,
                caseSwitching,
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
        private final List<Boolean> symbolicLabels;
        private final int sampleCount;
        private final int totalCases;
        private final int failureLimit;
        private final int caseSwitching;
        private final boolean run;

        private final TestCaseCreator testCaseCreator;

        public SingleTestConfiguration(
                String sourceFileName,
                InputFiles inputFiles,
                Map<Optimization, List<OptimizationLevel>> optimizationLevels,
                List<GenerationGoal> generationGoals,
                List<Boolean> symbolicLabels,
                int sampleCount,
                int failureLimit,
                int caseSwitching,
                boolean run) {
            this.sourceFileName = sourceFileName;
            this.inputFiles = inputFiles;
            this.optimizationLevels = optimizationLevels;
            this.generationGoals = generationGoals;
            this.symbolicLabels = symbolicLabels;
            this.sampleCount = sampleCount;
            this.failureLimit = failureLimit;
            this.caseSwitching = caseSwitching;
            this.run = run;

            this.totalCases = optimizationLevels.values().stream()
                    .mapToInt(List::size).reduce(1, Configuration::product)
                    * generationGoals.size()
                    * symbolicLabels.size()
                    * (caseSwitching + 1);

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

        public int getCaseSwitching() {
            return caseSwitching;
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
        public List<Boolean> getSymbolicLabels() {
            return symbolicLabels;
        }

        @Override
        public TestCaseCreator getTestCaseCreator() {
            return testCaseCreator;
        }

        public boolean isCaseSwitchingTest() {
            return caseSwitching > 0;
        }

        @Override
        public CompilerProfile createCompilerProfile(int testCase) {
            CompilerProfile profile = new CompilerProfile(false, OptimizationLevel.NONE)
                    .setOptimizationPasses(50)
                    .setAutoPrintflush(false)
                    .setRun(run);

            // Goal first
            int goal = testCase % generationGoals.size();
            profile.setGoal(generationGoals.get(goal));
            int remainder = testCase / generationGoals.size();

            // Symbolic labels second
            int labels = remainder % symbolicLabels.size();
            profile.setSymbolicLabels(symbolicLabels.get(labels));
            remainder /= symbolicLabels.size();

            // Case switching third
            int caseConfiguration = remainder % (caseSwitching + 1);
            profile.setCaseConfiguration(caseConfiguration);
            remainder /= (caseSwitching + 1);

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
