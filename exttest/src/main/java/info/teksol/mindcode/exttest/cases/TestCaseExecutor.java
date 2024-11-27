package info.teksol.mindcode.exttest.cases;

import info.teksol.emulator.processor.Assertion;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.MindcodeCompiler;
import info.teksol.mindcode.exttest.ErrorResult;
import info.teksol.mindcode.exttest.TestProgress;

import java.util.List;
import java.util.stream.Collectors;

public class TestCaseExecutor {
    private final TestProgress progress;

    public TestCaseExecutor(TestProgress progress) {
        this.progress = progress;
    }

    public void runTest(TestCaseCreator caseCreator, int testRunNumber) {
        MindcodeCompiler compiler = caseCreator.createCaseCompiler(testRunNumber);
        CompilerOutput<String> result = compiler.compile(List.of(caseCreator.getInputFile(testRunNumber)));

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
            if (compiler.getProfile().isRun() && (result.assertions().isEmpty()
                                                  || result.assertions().stream().anyMatch(Assertion::failure))) {
                progress.reportError(new ErrorResult(caseCreator.getTestCaseId(testRunNumber),
                        compiler.getProfile(), "", null, "No assertions found."));
            } else {
                progress.reportSuccess();
            }
        } else {
            progress.reportError(new ErrorResult(caseCreator.getTestCaseId(testRunNumber),
                    compiler.getProfile(), unexpectedMessages, result.executionException(), failedTests));
        }
    }
}
