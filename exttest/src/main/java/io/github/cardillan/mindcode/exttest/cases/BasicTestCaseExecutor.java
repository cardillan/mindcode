package io.github.cardillan.mindcode.exttest.cases;

import io.github.cardillan.mc.common.InputFile;
import io.github.cardillan.mc.emulator.Assertion;
import io.github.cardillan.mc.messages.MindcodeMessage;
import io.github.cardillan.mc.mindcode.compiler.MindcodeCompiler;
import io.github.cardillan.mindcode.exttest.ErrorResult;
import io.github.cardillan.mindcode.exttest.TestProgress;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;
import java.util.stream.Collectors;

@NullMarked
public class BasicTestCaseExecutor implements TestCaseExecutor {
    private final String testCaseId;
    private final InputFile inputFile;
    private final Supplier<MindcodeCompiler> compilerSupplier;

    public BasicTestCaseExecutor(String testCaseId, InputFile inputFile, Supplier<MindcodeCompiler> compilerSupplier) {
        this.testCaseId = testCaseId;
        this.inputFile = inputFile;
        this.compilerSupplier = compilerSupplier;
    }

    @Override
    public void runTest(TestProgress progress) {
        MindcodeCompiler compiler = compilerSupplier.get();

        try {
            compiler.compile(inputFile);

            String unexpectedMessages = compiler.getMessages().stream()
                    .filter(MindcodeMessage::isErrorOrWarning)
                    .map(MindcodeMessage::message)
                    .filter(msg -> !msg.contains("executable instructions has been exceeded."))
                    .collect(Collectors.joining("\n"));

            String failedTests = compiler.getAssertions().stream()
                    .filter(Assertion::failure)
                    .map(Assertion::generateErrorMessage)
                    .collect(Collectors.joining("\n"));

            boolean success = unexpectedMessages.isEmpty() && failedTests.isEmpty() && !compiler.hasRuntimeError();

            if (success) {
                if (compiler.globalCompilerProfile().isRun() && compiler.getAssertions().isEmpty()) {
                    progress.reportError(new ErrorResult(testCaseId, compiler.compilerProfile(), -1,
                            "", "No assertions found."));
                } else {
                    progress.reportSuccess();
                }
            } else {
                progress.reportError(new ErrorResult(testCaseId, compiler.compilerProfile(), -1,
                        unexpectedMessages, failedTests));
            }
        } catch (Exception e) {
            progress.reportError(new ErrorResult(testCaseId, compiler.compilerProfile(), -1,
                    "", "Exception: " + e));
        }
    }
}
