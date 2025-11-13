package info.teksol.mindcode.exttest;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;

@NullMarked
public interface TestConfiguration {
    int getThreads();
    int getTotalCases();
    int getSampleCount();
    int getFailureLimit();

    InputFiles getInputFiles();
    CompilerProfile createCompilerProfile(int testCase);

    boolean containsSetting(Enum<?> setting, Object value);

    String getSourceFileName();
    Path getResultPath();
    boolean isRun();

    int getCaseSwitching();
    TestConfiguration withCaseSwitching(int caseSwitching);
    boolean isCaseSwitchingTest();

    TestCaseCreator getTestCaseCreator();
}
