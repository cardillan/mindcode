package info.teksol.mindcode.exttest.cases;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.MindcodeCompiler;
import info.teksol.mindcode.exttest.TestConfiguration;
import info.teksol.mindcode.v3.InputFile;
import info.teksol.mindcode.v3.InputFiles;

public class TestCaseCreatorFull implements TestCaseCreator {
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
    public MindcodeCompiler createCaseCompiler(int testRunNumber) {
        InputFiles inputFiles = configuration.getInputFiles();
        CompilerProfile profile = configuration.createCompilerProfile(testRunNumber);
        return new MindcodeCompiler(profile, inputFiles);
    }

    @Override
    public InputFile getInputFile(int testRunNumber) {
        return configuration.getInputFiles().getMainInputFile();
    }

    @Override
    public String getTestCaseId(int testRunNumber) {
        return "Test case #" + testRunNumber;
    }
}
