package info.teksol.mindcode.exttest.cases;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mindcode.exttest.TestConfiguration;

import java.util.Objects;

public class TestCaseCreatorSampled implements TestCaseCreator {
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
    public MindcodeCompiler createCaseCompiler(int testRunNumber) {
        InputFiles inputFiles = configuration.getInputFiles();
        CompilerProfile profile = configuration.createCompilerProfile(getTestCaseNumber(testRunNumber));
        return new MindcodeCompiler(message -> {}, profile, inputFiles);
    }

    @Override
    public InputFile getInputFile(int testRunNumber) {
        return configuration.getInputFiles().getMainInputFile();
    }

    @Override
    public String getTestCaseId(int testRunNumber) {
        return "Test case #" + getTestCaseNumber(testRunNumber);
    }
}
