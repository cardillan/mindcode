package info.teksol.mindcode.exttest.cases;

import info.teksol.emulator.processor.Assertion;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.MindcodeCompiler;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.exttest.Code;
import info.teksol.mindcode.exttest.ErrorResult;
import info.teksol.mindcode.exttest.ExtendedTests;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.mindcode.v3.InputFile;
import info.teksol.mindcode.v3.InputFiles;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TestCaseExecutor {
    public final AtomicInteger testCount = new AtomicInteger(0);
    public final AtomicInteger errorCount = new AtomicInteger(0);
    public final Deque<ErrorResult> errors = new ConcurrentLinkedDeque<>();

    public CompilerProfile createCompilerProfile(int testCaseNumber) {
        CompilerProfile profile = new CompilerProfile(false)
                .setProcessorVersion(ProcessorVersion.MAX)
                .setAllOptimizationLevels(OptimizationLevel.NONE)
                .setOptimizationPasses(50)
                .setRun(true);

        for (int i = 0; i < ExtendedTests.LIST.size(); i++) {
            profile.setOptimizationLevel(ExtendedTests.LIST.get(i),
                    ((testCaseNumber & (1 << i))  == 0 ? OptimizationLevel.NONE : OptimizationLevel.ADVANCED));
        }

        return profile;
    }

    public void runTest(int testCaseNumber) {
        InputFiles inputFiles = InputFiles.create();
        CompilerProfile profile = createCompilerProfile(testCaseNumber);
        MindcodeCompiler compiler = new MindcodeCompiler(profile, inputFiles);;

        InputFile inputFile = inputFiles.registerSource(Code.code);
        CompilerOutput<String> result = compiler.compile(List.of(inputFile));

        String unexpectedMessages = result.messages().stream()
                .filter(MindcodeMessage::isErrorOrWarning)
                .map(MindcodeMessage::message)
                .collect(Collectors.joining("\n"));

        String failedTests = result.assertions().stream()
                .filter(Assertion::failure)
                .map(Assertion::generateErrorMessage)
                .collect(Collectors.joining("\n"));

        if (!unexpectedMessages.isEmpty() || !failedTests.isEmpty()) {
            errors.offer(new ErrorResult(testCaseNumber, profile.encode(), unexpectedMessages, failedTests));
            errorCount.incrementAndGet();
        }

        testCount.incrementAndGet();
    }
}
