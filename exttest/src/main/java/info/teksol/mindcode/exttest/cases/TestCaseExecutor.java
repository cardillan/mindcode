package info.teksol.mindcode.exttest.cases;

import info.teksol.emulator.processor.Assertion;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.MindcodeCompiler;
import info.teksol.mindcode.exttest.Configuration;
import info.teksol.mindcode.exttest.ErrorResult;
import info.teksol.mindcode.exttest.TestProgress;
import info.teksol.mindcode.v3.InputFiles;

import java.util.List;
import java.util.stream.Collectors;

public class TestCaseExecutor {
    private final Configuration.TestConfiguration configuration;
    private final TestProgress progress;

    public TestCaseExecutor(Configuration.TestConfiguration configuration, TestProgress progress) {
        this.configuration = configuration;
        this.progress = progress;
    }

    public void runTest(int testCaseNumber) {
        InputFiles inputFiles = configuration.getInputFiles();
        CompilerProfile profile = configuration.createCompilerProfile(testCaseNumber);
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

        boolean success = unexpectedMessages.isEmpty() && failedTests.isEmpty() && result.executionException() == null;

        if (success) {
            progress.reportSuccess();
        } else {
            progress.reportError(new ErrorResult(testCaseNumber, profile, unexpectedMessages,
                    result.executionException(), failedTests));
        }
    }
}
