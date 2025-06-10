package info.teksol.mindcode.exttest.cases;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mindcode.exttest.Configuration.SingleTestConfiguration;
import info.teksol.mindcode.exttest.TestConfiguration;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class TestCaseCreatorScreening extends BaseTestCaseCreator {
    private final List<SingleTestConfiguration> configurations;
    private final List<TestConfiguration> updatedConfigurations;

    public TestCaseCreatorScreening(List<SingleTestConfiguration> configurations,
            List<TestConfiguration> updatedConfigurations) {
        this.configurations = configurations;
        this.updatedConfigurations = updatedConfigurations;
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
    protected TestConfiguration getConfiguration(int testRunNumber) {
        return configurations.get(testRunNumber);
    }

    @Override
    protected InputFiles getInputFiles(int testRunNumber) {
        return configurations.get(testRunNumber).getInputFiles();
    }

    @Override
    protected CompilerProfile getCompilerProfile(int testRunNumber) {
        return configurations.get(testRunNumber).createCompilerProfile(0);
    }

    @Override
    public String getTestCaseId(int testRunNumber) {
        return "Screening test of file " + configurations.get(testRunNumber).getSourceFileName();
    }

    @Override
    protected void updateConfiguration(TestConfiguration configuration) {
        updatedConfigurations.add(configuration);
        //System.out.printf("    %s - case switching configurations: %d%n", configuration.getSourceFileName(), configuration.getCaseSwitching());
    }
}
