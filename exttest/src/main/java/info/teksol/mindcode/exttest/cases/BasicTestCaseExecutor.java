package info.teksol.mindcode.exttest.cases;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.emulator.Assertion;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mindcode.exttest.ErrorResult;
import info.teksol.mindcode.exttest.TestProgress;
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
