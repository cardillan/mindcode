package info.teksol.mindcode.exttest.cases;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mindcode.exttest.TestConfiguration;
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
            String caseId = getTestCaseId(testRunNumber) + (profile.getCaseConfiguration() == 0 ? "" : " (case " + profile.getCaseConfiguration() + ")" );
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
