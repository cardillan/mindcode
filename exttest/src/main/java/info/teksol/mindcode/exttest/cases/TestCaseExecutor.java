package info.teksol.mindcode.exttest.cases;

import info.teksol.emulator.processor.Assertion;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.MindcodeCompiler;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.exttest.ErrorResult;
import info.teksol.mindcode.exttest.TestConfiguration;
import info.teksol.mindcode.exttest.TestProgress;
import info.teksol.mindcode.v3.InputFiles;

import java.util.List;
import java.util.stream.Collectors;

public class TestCaseExecutor {
    private final TestConfiguration configuration;
    private final TestProgress progress;

    public TestCaseExecutor(TestConfiguration configuration, TestProgress progress) {
        this.configuration = configuration;
        this.progress = progress;
    }

    public CompilerProfile createCompilerProfile(int testCaseNumber) {
        CompilerProfile profile = new CompilerProfile(false)
                .setAllOptimizationLevels(OptimizationLevel.NONE)
                .setOptimizationPasses(50)
                .setRun(false);

        configuration.setupProfile(profile, testCaseNumber);
        return profile;
    }

    public void runTest(int testCaseNumber) {
        InputFiles inputFiles = configuration.getInputFiles();
        CompilerProfile profile = createCompilerProfile(testCaseNumber);
        MindcodeCompiler compiler = new MindcodeCompiler(profile, inputFiles);

        CompilerOutput<String> result = compiler.compile(List.of(inputFiles.getMainInputFile()));

        String unexpectedMessages = result.messages().stream()
                .filter(MindcodeMessage::isErrorOrWarning)
                .map(MindcodeMessage::message)
                .collect(Collectors.joining("\n"));

        String failedTests = result.assertions().stream()
                .filter(Assertion::failure)
                .map(Assertion::generateErrorMessage)
                .collect(Collectors.joining("\n"));

        boolean ok = unexpectedMessages.isEmpty() && failedTests.isEmpty();
        if (!ok) {
            progress.errors.offer(new ErrorResult(testCaseNumber, profile, unexpectedMessages, failedTests));
            progress.errorCount.incrementAndGet();
        }

        progress.finishedCount.incrementAndGet();
    }
}
