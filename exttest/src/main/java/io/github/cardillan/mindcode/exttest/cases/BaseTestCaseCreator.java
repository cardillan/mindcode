package io.github.cardillan.mindcode.exttest.cases;

import io.github.cardillan.mindcode.common.InputFiles;
import io.github.cardillan.mindcode.compiler.MindcodeCompiler;
import io.github.cardillan.mindcode.exttest.TestConfiguration;
import io.github.cardillan.mindcode.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class BaseTestCaseCreator implements TestCaseCreator {
    protected abstract TestConfiguration getConfiguration(int testRunNumber);
    protected abstract InputFiles getInputFiles(int testRunNumber);
    protected abstract CompilerProfile getCompilerProfile(int testRunNumber);
    protected abstract String getTestCaseId(int testRunNumber);

    protected void updateConfiguration(TestConfiguration configuration) {
        // Do nothing by default
    }

    @Override
    public TestCaseExecutor createExecutor(int testRunNumber) {
        TestConfiguration configuration = getConfiguration(testRunNumber);
        InputFiles inputFiles = getInputFiles(testRunNumber);
        CompilerProfile profile = getCompilerProfile(testRunNumber);
        if (configuration.isCaseSwitchingTest()) {
            String caseId = getTestCaseId(testRunNumber);
            return new CaseSwitchingTestCaseExecutor(caseId, inputFiles.getMainInputFile(),
                    () -> createCompiler(inputFiles, testRunNumber),
                    caseSwitching -> updateConfiguration(configuration.withCaseSwitching(caseSwitching))
            );
        } else {
            updateConfiguration(configuration);
            return new BasicTestCaseExecutor(getTestCaseId(testRunNumber), inputFiles.getMainInputFile(),
                    () -> createCompiler(inputFiles, testRunNumber));
        }
    }

    private MindcodeCompiler createCompiler(InputFiles inputFiles, int testRunNumber) {
        CompilerProfile profile = getCompilerProfile(testRunNumber);
        return new MindcodeCompiler(message -> {}, profile, inputFiles);
    }
}
