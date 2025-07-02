package info.teksol.mindcode.exttest.cases;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.emulator.processor.Assertion;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.optimization.CaseSwitcher.ConvertCaseExpressionAction;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.compiler.optimization.cases.CaseSwitcherConfigurations;
import info.teksol.mc.profile.GenerationGoal;
import info.teksol.mindcode.exttest.ErrorResult;
import info.teksol.mindcode.exttest.TestProgress;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@NullMarked
public class CaseSwitchingTestCaseExecutor implements TestCaseExecutor {
    private static final int STRENGTH = 5;

    private final String testCaseId;
    private final InputFile inputFile;
    private final Supplier<MindcodeCompiler> compilerSupplier;
    private final IntConsumer caseSwitcherCountConsumer;

    public CaseSwitchingTestCaseExecutor(String testCaseId, InputFile inputFile, Supplier<MindcodeCompiler> compilerSupplier,
            IntConsumer caseSwitcherCountConsumer) {
        this.testCaseId = testCaseId;
        this.inputFile = inputFile;
        this.compilerSupplier = compilerSupplier;
        this.caseSwitcherCountConsumer = caseSwitcherCountConsumer;
    }

    @Override
    public void runTest(TestProgress progress) {
        MindcodeCompiler compiler = compilerSupplier.get();
        try {
            compiler.globalCompilerProfile()
                    .setOptimizationLevel(Optimization.CASE_SWITCHING, OptimizationLevel.NONE)
                    .setCaseOptimizationStrength(STRENGTH);
            if (!compile(compiler, progress)) return;
            int originalSteps = compiler.getEmulator().getSteps();
            int originalSize = compiler.getInstructions().size();
            String originalOutput = compiler.getEmulator().getTextBuffer().getFormattedOutput();

            // Second round
            compiler = compilerSupplier.get();
            compiler.globalCompilerProfile()
                    .setGoal(GenerationGoal.SPEED)
                    .setCaseOptimizationStrength(STRENGTH);
            if (!compile(compiler, progress)) return;
            int newSteps = compiler.getEmulator().getSteps();
            int newSize = compiler.getInstructions().size();
            String newOutput = compiler.getEmulator().getTextBuffer().getFormattedOutput();

            List<CaseSwitcherConfigurations> configurationData = compiler.getDiagnosticData(CaseSwitcherConfigurations.class);
            if (configurationData.size() == 1) {
                caseSwitcherCountConsumer.accept(configurationData.getFirst().configurationsCount());
            }

            if (!Objects.equals(originalOutput, newOutput)) {
                progress.reportError(new ErrorResult(testCaseId,
                        compiler.globalCompilerProfile(), "", compiler.getExecutionException(),
                        "The original and optimized outputs differ:\n" + originalOutput + "\n" + newOutput));
            }

            List<ConvertCaseExpressionAction> diagnosticData = compiler.getDiagnosticData(ConvertCaseExpressionAction.class)
                    .stream().filter(ConvertCaseExpressionAction::applied).toList();
            if (diagnosticData.size() != 1) {
                progress.reportError(new ErrorResult(testCaseId,
                        compiler.globalCompilerProfile(), "", compiler.getExecutionException(), "No Case-Switching diagnostic information found."));
            } else {
                ConvertCaseExpressionAction action = diagnosticData.getFirst();
                int blockCount = compiler.metadata().getBlockCount();
                int stepDifference = originalSteps - newSteps;
                int expectedStepDifference = action.originalSteps() - action.executionSteps();
                if (stepDifference != expectedStepDifference) {
                    progress.reportError(new ErrorResult(testCaseId,
                            compiler.globalCompilerProfile(), "", compiler.getExecutionException(),
                            String.format("Original steps: %d, new steps: %d, difference: %d (expected %d).",
                                    originalSteps, newSteps, stepDifference, expectedStepDifference)));
                }

                int sizeDifference = newSize - originalSize;
                int expectedSizeDifference = action.cost();
                if (sizeDifference != expectedSizeDifference) {
                    progress.reportError(new ErrorResult(testCaseId,
                            compiler.globalCompilerProfile(), "", compiler.getExecutionException(),
                            String.format("Original size: %d, new size: %d, difference: %d (expected %d)",
                                    originalSize, newSize, sizeDifference, expectedSizeDifference)));
                }

                if (stepDifference == expectedStepDifference && sizeDifference == expectedSizeDifference) {
                    progress.reportSuccess();
                }
//                progress.reportSuccess();
            }
        } catch (Exception e) {
            progress.reportError(new ErrorResult(testCaseId,
                    compiler.globalCompilerProfile(), "", null, "Exception: " + e));
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
                    compiler.globalCompilerProfile(), unexpectedMessages, compiler.getExecutionException(), failedTests));
        }

        return success;
    }
}
