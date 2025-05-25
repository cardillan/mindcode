package info.teksol.mindcode.exttest.cases;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mindcode.exttest.TestConfiguration;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class TestCaseCreatorSampled extends BaseTestCaseCreator {
    private final TestConfiguration configuration;
    private final long firstCase;
    private final long totalCases;
    private final long sampleCount;

    TestCaseCreatorSampled(TestConfiguration configuration, int totalCases, int sampleCount) {
        this.configuration = configuration;
        this.totalCases = totalCases;
        this.sampleCount = sampleCount;
        firstCase = totalCases / sampleCount / 2;
    }

    public TestCaseCreatorSampled(TestConfiguration configuration) {
        this(Objects.requireNonNull(configuration),
                configuration.getTotalCases(), configuration.getSampleCount());
    }

    @Override
    public int getTotalCases() {
        return (int) totalCases;
    }

    @Override
    public int getSampleCount() {
        return (int) sampleCount;
    }

    int getTestCaseNumber(int testRunNumber) {
        return (int) (firstCase + (totalCases * testRunNumber) / sampleCount);
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
        return configuration.createCompilerProfile(getTestCaseNumber(testRunNumber));
    }

    @Override
    public String getTestCaseId(int testRunNumber) {
        return "Test case #" + getTestCaseNumber(testRunNumber);
    }
}
