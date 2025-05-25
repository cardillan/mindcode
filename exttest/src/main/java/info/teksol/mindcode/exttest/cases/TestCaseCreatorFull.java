package info.teksol.mindcode.exttest.cases;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mindcode.exttest.TestConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TestCaseCreatorFull extends BaseTestCaseCreator {
    private final TestConfiguration configuration;

    public TestCaseCreatorFull(TestConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public int getTotalCases() {
        return configuration.getTotalCases();
    }

    @Override
    public int getSampleCount() {
        return configuration.getTotalCases();
    }

    @Override
    protected TestConfiguration getConfiguration(int testRunNumber) {
        return configuration;
    }

    @Override
    protected InputFiles getInputFiles(int testRunNumber) {
        return configuration.getInputFiles();
    }

    @Override
    protected CompilerProfile getCompilerProfile(int testRunNumber) {
        return configuration.createCompilerProfile(testRunNumber);
    }

    @Override
    public String getTestCaseId(int testRunNumber) {
        return "Test case #" + testRunNumber;
    }
}
