package info.teksol.mindcode.exttest;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import info.teksol.mindcode.exttest.cases.TestCaseCreatorFull;
import info.teksol.mindcode.exttest.cases.TestCaseCreatorSampled;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

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
            SequencedMap<Enum<?>, List<Object>> settings,
            int sampleCount,
            int failureLimit,
            int caseSwitching,
            boolean run) {

        configurations.add(new SingleTestConfiguration(
                sourceFileName,
                inputFiles,
                settings,
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
        private final SequencedMap<Enum<?>, List<Object>> settings;
        private final int sampleCount;
        private final int totalCases;
        private final int failureLimit;
        private final int caseSwitching;
        private final boolean run;

        private final TestCaseCreator testCaseCreator;

        public SingleTestConfiguration(
                String sourceFileName,
                InputFiles inputFiles,
                SequencedMap<Enum<?>, List<Object>> settings,
                int sampleCount,
                int failureLimit,
                int caseSwitching,
                boolean run) {
            this.sourceFileName = sourceFileName;
            this.inputFiles = inputFiles;
            this.settings = settings;
            this.sampleCount = sampleCount;
            this.failureLimit = failureLimit;
            this.caseSwitching = caseSwitching;
            this.run = run;

            this.totalCases = settings.values().stream()
                    .mapToInt(List::size).reduce(1, Configuration::product)
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
        public boolean containsSetting(Enum<?> setting, Object value) {
            return settings.getOrDefault(setting, List.of()).contains(value);
        }

        @Override
        public TestCaseCreator getTestCaseCreator() {
            return testCaseCreator;
        }

        public boolean isCaseSwitchingTest() {
            return caseSwitching > 0;
        }

        public TestConfiguration withCaseSwitching(int caseSwitching) {
            return new SingleTestConfiguration(sourceFileName, inputFiles, settings, sampleCount, failureLimit, caseSwitching, run);
        }

        @Override
        public CompilerProfile createCompilerProfile(int testCase) {
            CompilerProfile profile = CompilerProfile.noOptimizations(false)
                    .setOptimizationPasses(50)
                    .setAutoPrintflush(false)
                    .setRun(run);

            int current = testCase;
            for (Map.Entry<Enum<?>, List<Object>> entry : settings.entrySet()) {
                int index = current % entry.getValue().size();
                profile.getOption(entry.getKey()).setValue(entry.getValue().get(index));
                current /= entry.getValue().size();
            }

            // And case switching
            int caseConfiguration = current % (caseSwitching + 1);
            profile.setCaseConfiguration(caseConfiguration);
            current /= (caseSwitching + 1);

            if (current != 0) {
                throw new IllegalArgumentException("Invalid test case number " + testCase);
            }

            return profile;
        }
    }
}
