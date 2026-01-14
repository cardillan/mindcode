package info.teksol.mc.mindcode.tests;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.emulator.Assertion;
import info.teksol.mc.emulator.Emulator;
import info.teksol.mc.emulator.ExecutionFlag;
import info.teksol.mc.emulator.ExecutorResults;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.blocks.MemoryBlock;
import info.teksol.mc.emulator.blocks.MindustryBuilding;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.messages.TimingMessage;
import info.teksol.mc.mindcode.compiler.AbstractCompilerTestBase;
import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.util.CRC64;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

// Base class for algorithm tests
// Processor for execution is equipped with bank1 memory bank.
// Additional blocks can be added
@NullMarked
public abstract class AbstractProcessorTest extends AbstractCompilerTestBase {

    protected final String logSuffix;
    protected final boolean symbolicLabels;

    public AbstractProcessorTest() {
        this.symbolicLabels = getClass().getSimpleName().contains("SymbolicLabels");
        boolean translation = getClass().getSimpleName().contains("Translation");
        logSuffix = symbolicLabels ? "-symbolic.log" : translation ? "-translation.log" : ".log";
    }

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.EMULATOR;
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile()
                .setDebugMessages(3)
                // Messes out code coverage otherwise
                .setSignature(false)
                // Do not remove end instructions
                .setOptimizationLevel(Optimization.JUMP_THREADING, OptimizationLevel.BASIC)
                // Do not merge constants in print statements
                .setOptimizationLevel(Optimization.PRINT_MERGING, OptimizationLevel.BASIC)
                .setExecutionFlag(ExecutionFlag.DUMP_VARIABLES_ON_STOP, false)
                .setSymbolicLabels(symbolicLabels)
                .setRun(true);
    }

    protected abstract String getScriptsDirectory();

    private static final Map<String, String> headers = new ConcurrentHashMap<>();
    private static final Map<String, Queue<String>> results = new ConcurrentHashMap<>();

    // Handles evaluating of expected vs. actual program output
    @FunctionalInterface
    protected interface RunEvaluator {
        /// Called to compare the actual results of running a code with an expected state.
        ///
        /// @param useAsserts if true, the evaluator should explicitly assert the equality, otherwise it just reports
        ///                                     the result. Each evaluator will be called at least once with useAsserts set to true.
        /// @param emulator   processor emulator used to run the code
        /// @return true if the actual results output matches the expected one
        boolean asExpected(boolean useAsserts, Emulator emulator);
    }

    static void done(String scriptsDirectory, String className) throws IOException {
        if (results.containsKey(className)) {
            Path path = Path.of(scriptsDirectory, className + ".txt");
            String[] array = results.get(className).toArray(new String[0]);
            List<String> texts = Stream.of(array).sorted().collect(Collectors.toCollection(ArrayList::new));
            texts.addFirst(headers.get(className));
            Files.write(path, texts);
        }
    }

    private void logCompilation(@Nullable String title, String code, String compiled, int instructions) {
        String name = title != null ? title : testInfo().getDisplayName().replaceAll("\\(\\)", "");
        headers.computeIfAbsent(getClass().getSimpleName(), k ->
                String.format("%-40s %12s   %-16s   %-16s", "Name", "Instructions", "Source CRC", "Compiled CRC"));
        String info = String.format(Locale.US,
                "%-40s %12d   %016X   %016X",
                name + ":", instructions, CRC64.hash1(code), CRC64.hash1(compiled));
        System.out.println(info);
        results.computeIfAbsent(getClass().getSimpleName(), k -> new ConcurrentLinkedDeque<>()).add(info);
    }

    private void logPerformance(@Nullable String title, String code, String compiled, Emulator emulator) {
        ExecutorResults result = emulator.getExecutorResults(0);
        long coverage = Math.round(1000 * result.getCoverage());
        String name = title != null ? title : testInfo().getDisplayName().replaceAll("\\(\\)", "");
        headers.computeIfAbsent(getClass().getSimpleName(), k ->
                String.format("%-40s %12s   %6s   %8s   %-16s   %-16s", "Name", "Instructions", "Steps", "Coverage", "Source CRC", "Compiled CRC"));
        String info = String.format(Locale.US,
                "%-40s %12d   %6d   %5d.%01d%%   %016X   %016X",
                name + ":", result.getProfile().length, result.getSteps(), coverage / 10, coverage % 10,
                CRC64.hash1(code.getBytes(StandardCharsets.UTF_8)),
                CRC64.hash1(compiled.getBytes(StandardCharsets.UTF_8)));
        System.out.println(info);
        results.computeIfAbsent(getClass().getSimpleName(), k -> new ConcurrentLinkedDeque<>()).add(info);
    }

    // Prevent unit tests hanging due to possible endless loops in generated code
    protected final int STEP_LIMIT = 1_000_000;

    private @Nullable TestInfo testInfo;

    private TestInfo testInfo() {
        return Objects.requireNonNull(testInfo);
    }

    @BeforeEach
    void init(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    private void writeLogFile(@Nullable Path logFile, MindcodeCompiler compiler, List<LogicInstruction> instructions) {
        if (logFile == null) {
            return;
        }

        List<String> data = compiler.getMessages().stream()
                .filter(m -> !(m instanceof TimingMessage))
                .map(MindcodeMessage::message)
                .collect(Collectors.toCollection(ArrayList::new));

        data.add("\nFinal code before resolving virtual instructions:\n");
        instructions.stream().map(LogicInstruction::toString).forEach(data::add);
        data.add("");
        String joined = String.join("\n", data)
                .replaceAll("\\R", System.lineSeparator());

        try {
            Files.writeString(logFile, joined);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void compileAndOutputCode(String title, String code, Path logFile) {
        InputFiles inputFiles = createInputFiles(code);
        MindcodeCompiler compiler = new MindcodeCompiler(expectedMessages(),
                createCompilerProfile().setRun(false), inputFiles);
        compiler.compile();
        if (compiler.getUnresolved().isEmpty()) {
            throw new MindcodeInternalError("No code generated.");
        }
        logCompilation(title, code, compiler.getOutput(), compiler.getExecutableInstructions().size());
        writeLogFile(logFile, compiler, compiler.getUnresolved());
    }

    protected void compileAndOutputFile(String fileName) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + logSuffix);
        compileAndOutputCode(fileName, readFile(fileName), logFile);
    }


    protected void setupLogicBlock(LogicBlock logicBlock, Map<String, MindustryBuilding> blocks) {
        logicBlock.addBlock("bank1", MemoryBlock.createMemoryBank(ip.getMetadata()));
        logicBlock.addBlock("bank2", MemoryBlock.createMemoryBank(ip.getMetadata()));
        blocks.forEach(logicBlock::addBlock);
    }

    protected void testAndEvaluateCode(@Nullable String title, ExpectedMessages expectedMessages, String code,
            Map<String, MindustryBuilding> blocks, RunEvaluator evaluator, @Nullable Path logFile) {
        process(expectedMessages,
                createInputFiles(code),
                emulator -> setupLogicBlock(emulator, blocks),
                compiler -> {
                    assertFalse(compiler.hasErrors(), "Errors while compiling program.");
                    writeLogFile(logFile, compiler, compiler.getUnresolved());
                    logPerformance(title, code, compiler.getOutput(), compiler.getEmulator());

                    List<Executable> executables = new ArrayList<>();
                    executables.add(() -> evaluator.asExpected(true, compiler.getEmulator()));
                    compiler.getAssertions().forEach(a -> executables.add(() -> assertTrue(a.success(), a.generateErrorMessage())));
                    executables.add(() -> assertFalse(compiler.isRuntimeError()));
                    assertAll(executables);

                    return null;
                });
    }

    protected RunEvaluator outputEvaluator(List<String> expectedOutput) {
        return (useAsserts, emulator) -> {
            List<String> actualOutput = emulator.getExecutorResults(0).getPrintOutput();
            boolean outputMatches = Objects.equals(expectedOutput, actualOutput);
            if (useAsserts) {
                assertEquals(expectedOutput, actualOutput);
            }

            boolean assertionsMatch = emulator.getAllAssertions().stream().allMatch(Assertion::success);
            if (useAsserts && !assertionsMatch) {
                emulator.getAllAssertions().stream().filter(Assertion::failure).forEach(a -> fail(a.generateErrorMessage()));
            }
            return outputMatches && assertionsMatch;
        };
    }

    protected RunEvaluator containsOutputEvaluator(String expectedOutput) {
        return (useAsserts, emulator) -> {
            String actualOutput = emulator.getExecutorResults(0).getFormattedOutput();
            boolean outputMatches = actualOutput.contains(expectedOutput);
            if (useAsserts) {
                assertTrue(outputMatches, "Output does not contain expected text.\nExpected:" + expectedOutput + "\nOutput: " + actualOutput);
            }

            boolean assertionsMatch = emulator.getAllAssertions().stream().allMatch(Assertion::success);
            if (useAsserts && !assertionsMatch) {
                emulator.getAllAssertions().stream().filter(Assertion::failure).forEach(a -> fail(a.generateErrorMessage()));
            }
            return outputMatches && assertionsMatch;
        };
    }

    protected RunEvaluator lastOutputEvaluator(String expectedLastOutput) {
        return (useAsserts, emulator) -> {
            String actualLastOutput = emulator.getExecutorResults(0).getPrintOutput().getLast();
            boolean outputMatches = Objects.equals(expectedLastOutput, actualLastOutput);
            if (useAsserts) {
                assertEquals(expectedLastOutput, actualLastOutput);
            }

            boolean assertionsMatch = emulator.getAllAssertions().stream().allMatch(Assertion::success);
            if (useAsserts && !assertionsMatch) {
                emulator.getAllAssertions().stream().filter(Assertion::failure).forEach(a -> fail(a.generateErrorMessage()));
            }
            return outputMatches && assertionsMatch;
        };
    }

    protected RunEvaluator assertEvaluator() {
        return (useAsserts, processor) -> {
            if (useAsserts) {
                if (processor.getAllAssertions().isEmpty()) {
                    fail("Expected assertions not present");
                }
            }
            return !processor.getAllAssertions().isEmpty();
        };
    }

    protected void testAndEvaluateFile(String fileName, Map<String, MindustryBuilding> blocks,
            ExpectedMessages expectedMessages, RunEvaluator evaluator) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + logSuffix);
        testAndEvaluateCode(fileName, expectedMessages, readFile(fileName), blocks, evaluator, logFile);
    }

    protected void testAndEvaluateFile(String fileName, Function<String, String> codeDecorator,
            Map<String, MindustryBuilding> blocks, RunEvaluator evaluator) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + logSuffix);
        testAndEvaluateCode(fileName, expectedMessages(), codeDecorator.apply(readFile(fileName)), blocks, evaluator, logFile);
    }

    protected void testAndEvaluateFile(String fileName, Function<String, String> codeDecorator,
            Map<String, MindustryBuilding> blocks, List<String> expectedOutputs) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + logSuffix);
        testAndEvaluateCode(fileName, expectedMessages(), codeDecorator.apply(readFile(fileName)), blocks,
                outputEvaluator(expectedOutputs), logFile);
    }

    protected void testAndEvaluateFile(String fileName, List<String> expectedOutputs) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + logSuffix);
        testAndEvaluateCode(fileName, expectedMessages(), readFile(fileName), Map.of(),
                outputEvaluator(expectedOutputs), logFile);
    }

    protected void testAndEvaluateFile(String fileName) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + logSuffix);
        testAndEvaluateCode(fileName, expectedMessages(), readFile(fileName), Map.of(), assertEvaluator(), logFile);
    }

    protected void testCode(ExpectedMessages expectedMessages, String code, Map<String, MindustryBuilding> blocks, String... expectedOutputs) {
        testAndEvaluateCode(null, expectedMessages, code, blocks, outputEvaluator(List.of(expectedOutputs)), null);
    }

    protected void testCode(ExpectedMessages expectedMessages, String code, String... expectedOutputs) {
        testAndEvaluateCode(null, expectedMessages, code, Map.of(), outputEvaluator(List.of(expectedOutputs)), null);
    }

    protected void testCode(String code, Map<String, MindustryBuilding> blocks, String... expectedOutputs) {
        testAndEvaluateCode(null, expectedMessages(), code, blocks, outputEvaluator(List.of(expectedOutputs)), null);
    }

    protected void testCode(String code, String... expectedOutputs) {
        testAndEvaluateCode(null, expectedMessages(), code, Map.of(), outputEvaluator(List.of(expectedOutputs)), null);
    }

    protected void testAssertions(String code) {
        testAndEvaluateCode(null, expectedMessages(), code, Map.of(), assertEvaluator(), null);
    }
}
