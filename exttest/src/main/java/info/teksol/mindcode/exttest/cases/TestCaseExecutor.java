package info.teksol.mindcode.exttest.cases;

import info.teksol.mc.emulator.processor.Assertion;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mindcode.exttest.ErrorResult;
import info.teksol.mindcode.exttest.TestProgress;

import java.util.stream.Collectors;

public class TestCaseExecutor {
    private final TestProgress progress;

    public TestCaseExecutor(TestProgress progress) {
        this.progress = progress;
    }

    public void runTest(TestCaseCreator caseCreator, int testRunNumber) {

        MindcodeCompiler compiler = caseCreator.createCaseCompiler(testRunNumber);
        compiler.compile(caseCreator.getInputFile(testRunNumber));

        String unexpectedMessages = compiler.getMessages().stream()
                .filter(MindcodeMessage::isErrorOrWarning)
                .map(MindcodeMessage::message)
                .collect(Collectors.joining("\n"));

        String failedTests = compiler.getAssertions().stream()
                .filter(Assertion::failure)
                .map(Assertion::generateErrorMessage)
                .collect(Collectors.joining("\n"));

        boolean success = unexpectedMessages.isEmpty() && failedTests.isEmpty() && compiler.getExecutionException() == null;

        if (success) {
            if (compiler.compilerProfile().isRun() && (compiler.getAssertions().isEmpty()
                                                       || compiler.getAssertions().stream().anyMatch(Assertion::failure))) {
                progress.reportError(new ErrorResult(caseCreator.getTestCaseId(testRunNumber),
                        compiler.compilerProfile(), "", null, "No assertions found."));
            } else {
                progress.reportSuccess();
            }
        } else {
            progress.reportError(new ErrorResult(caseCreator.getTestCaseId(testRunNumber),
                    compiler.compilerProfile(), unexpectedMessages, compiler.getExecutionException(), failedTests));
        }
    }
}
