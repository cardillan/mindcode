package info.teksol.mindcode.exttest;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import info.teksol.mindcode.v3.InputFiles;

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
