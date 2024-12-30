package info.teksol.mindcode.exttest;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;

import java.util.List;
import java.util.Map;

public interface TestConfiguration {
    int getThreads();
    int getTotalCases();
    int getSampleCount();
    int getFailureLimit();


    InputFiles getInputFiles();
    CompilerProfile createCompilerProfile(int testCase);

    Map<Optimization, List<OptimizationLevel>> getOptimizationLevels();
    List<GenerationGoal> getGenerationGoals();

    TestCaseCreator getTestCaseCreator();
}
