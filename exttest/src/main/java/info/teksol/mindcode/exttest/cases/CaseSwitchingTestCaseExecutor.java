package info.teksol.mindcode.exttest.cases;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.emulator.processor.Assertion;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.optimization.CaseSwitcher;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.exttest.ErrorResult;
import info.teksol.mindcode.exttest.TestProgress;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@NullMarked
public class CaseSwitchingTestCaseExecutor implements TestCaseExecutor {
    private final String testCaseId;
    private final InputFile inputFile;
    private final Supplier<MindcodeCompiler> compilerSupplier;

    public CaseSwitchingTestCaseExecutor(String testCaseId, InputFile inputFile, Supplier<MindcodeCompiler> compilerSupplier) {
        this.testCaseId = testCaseId;
        this.inputFile = inputFile;
        this.compilerSupplier = compilerSupplier;
    }

    @Override
    public void runTest(TestProgress progress) {
        MindcodeCompiler compiler = compilerSupplier.get();
        try {
            compiler.compilerProfile()
                    .setOptimizationLevel(Optimization.CASE_SWITCHING, OptimizationLevel.NONE)
                    .setCaseOptimizationStrength(256);
            if (!compile(compiler, progress)) return;
            int originalSteps = compiler.getEmulator().getSteps();
            int originalSize = compiler.getInstructions().size();
            String originalOutput = compiler.getEmulator().getTextBuffer().getFormattedOutput();

            // Second round
            compiler = compilerSupplier.get();
            compiler.compilerProfile().setCaseOptimizationStrength(256);
            if (!compile(compiler, progress)) return;
            int newSteps = compiler.getEmulator().getSteps();
            int newSize = compiler.getInstructions().size();
            String newOutput = compiler.getEmulator().getTextBuffer().getFormattedOutput();

            if (!Objects.equals(originalOutput, newOutput)) {
                progress.reportError(new ErrorResult(testCaseId,
                        compiler.compilerProfile(), "", compiler.getExecutionException(),
                        "The original and optimized outputs differ:\n" + originalOutput + "\n" + newOutput));
            }

            List<CaseSwitcher.ConvertCaseExpressionAction> diagnosticData = compiler.getDiagnosticData(CaseSwitcher.ConvertCaseExpressionAction.class);
            if (diagnosticData.size() != 1) {
                progress.reportError(new ErrorResult(testCaseId,
                        compiler.compilerProfile(), "", compiler.getExecutionException(), "No Case-Switching diagnostic information found."));
            } else {
                int blockCount = compiler.metadata().getBlockCount();
                int stepDifference = originalSteps - newSteps;
                int expectedStepDifference = (int) Math.round(blockCount * diagnosticData.getFirst().rawBenefit());
                if (stepDifference != expectedStepDifference) {
                    progress.reportError(new ErrorResult(testCaseId,
                            compiler.compilerProfile(), "", compiler.getExecutionException(),
                            String.format("Original steps: %d, new steps: %d, difference: %d (expected %d).",
                                    originalSteps, newSteps, stepDifference, expectedStepDifference)));
                }

                int sizeDifference = newSize - originalSize;
                int expectedSizeDifference = diagnosticData.getFirst().cost();
                if (sizeDifference != expectedSizeDifference) {
                    progress.reportError(new ErrorResult(testCaseId,
                            compiler.compilerProfile(), "", compiler.getExecutionException(),
                            String.format("Original size: %d, new size: %d, difference: %d (expected %d).",
                                    originalSize, newSize, sizeDifference, expectedSizeDifference)));
                }

                if (stepDifference == expectedStepDifference && sizeDifference == expectedSizeDifference) {
                    progress.reportSuccess();
                }
            }
        } catch (Exception e) {
            progress.reportError(new ErrorResult(testCaseId,
                    compiler.compilerProfile(), "", null, "Exception: " + e));
        }
    }

    private boolean compile(MindcodeCompiler compiler, TestProgress progress) {
        compiler.compile(inputFile);

        String unexpectedMessages = compiler.getMessages().stream()
                .filter(MindcodeMessage::isErrorOrWarning)
                .map(MindcodeMessage::message)
                .collect(Collectors.joining("\n"));

        String failedTests = compiler.getAssertions().stream()
                .filter(Assertion::failure)
                .map(Assertion::generateErrorMessage)
                .collect(Collectors.joining("\n"));

        boolean success = unexpectedMessages.isEmpty() && failedTests.isEmpty() && compiler.getExecutionException() == null;

        if (!success) {
            progress.reportError(new ErrorResult(testCaseId,
                    compiler.compilerProfile(), unexpectedMessages, compiler.getExecutionException(), failedTests));
        }

        return success;
    }
}
