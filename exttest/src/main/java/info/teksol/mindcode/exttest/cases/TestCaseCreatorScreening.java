package info.teksol.mindcode.exttest.cases;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.MindcodeCompiler;
import info.teksol.mindcode.exttest.Configuration.SingleTestConfiguration;
import info.teksol.mindcode.v3.InputFile;
import info.teksol.mindcode.v3.InputFiles;

import java.util.List;

public class TestCaseCreatorScreening implements TestCaseCreator {
    private final List<SingleTestConfiguration> configurations;

    public TestCaseCreatorScreening(List<SingleTestConfiguration> configurations) {
        this.configurations = configurations;
    }

    @Override
    public int getTotalCases() {
        return configurations.size();
    }

    @Override
    public int getSampleCount() {
        return configurations.size();
    }

    @Override
    public MindcodeCompiler createCaseCompiler(int testRunNumber) {
        InputFiles inputFiles = configurations.get(testRunNumber).getInputFiles();
        CompilerProfile profile = configurations.get(testRunNumber).createCompilerProfile(0);
        return new MindcodeCompiler(profile, inputFiles);
    }

    @Override
    public InputFile getInputFile(int testRunNumber) {
        return configurations.get(testRunNumber).getInputFiles().getMainInputFile();
    }


    @Override
    public String getTestCaseId(int testRunNumber) {
        return "Screening test of file " + configurations.get(testRunNumber).getSourceFileName();
    }
}