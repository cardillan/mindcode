package info.teksol.mc.mindcode.tests;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.emulator.blocks.Memory;
import info.teksol.mc.emulator.blocks.MindustryBlock;
import info.teksol.mc.emulator.processor.Assertion;
import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.emulator.processor.Processor;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.messages.TimingMessage;
import info.teksol.mc.mindcode.compiler.AbstractTestBase;
import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.compiler.postprocess.LogicInstructionPrinter;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.profile.CompilerProfile;
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
public abstract class AbstractProcessorTest extends AbstractTestBase {

    public static final String SCRIPTS_BASE_DIRECTORY = "src/test/resources/info/teksol/mc/mindcode/tests";

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.EMULATOR;
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
        ///                                     the result. Each evaluator will be called at lest once with useAsserts set to true.
        /// @param compiler   MindcodeCompiler instance used to compile and run the code
        /// @return true if the actual results output matches the expected one
        boolean asExpected(boolean useAsserts, Processor processor);
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
                name + ":", instructions,
                CRC64.hash1(code.getBytes(StandardCharsets.UTF_8)),
                CRC64.hash1(compiled.getBytes(StandardCharsets.UTF_8)));
        System.out.println(info);
        results.computeIfAbsent(getClass().getSimpleName(), k -> new ConcurrentLinkedDeque<>()).add(info);
    }

    private void logPerformance(@Nullable String title, String code, String compiled, Processor processor) {
        int coverage = 1000 * processor.getCoverage().cardinality() / processor.getInstructions();
        String name = title != null ? title : testInfo().getDisplayName().replaceAll("\\(\\)", "");
        headers.computeIfAbsent(getClass().getSimpleName(), k ->
                String.format("%-40s %12s   %6s   %8s   %-16s   %-16s", "Name", "Instructions", "Steps", "Coverage", "Source CRC", "Compiled CRC"));
        String info = String.format(Locale.US,
                "%-40s %12d   %6d   %5d.%01d%%   %016X   %016X",
                name + ":", processor.getInstructions(), processor.getSteps(), coverage / 10, coverage % 10,
                CRC64.hash1(code.getBytes(StandardCharsets.UTF_8)),
                CRC64.hash1(compiled.getBytes(StandardCharsets.UTF_8)));
        System.out.println(info);
        results.computeIfAbsent(getClass().getSimpleName(), k -> new ConcurrentLinkedDeque<>()).add(info);
    }

    // Prevent unit tests hanging due to possible endless loops in generated code
    protected final int MAX_STEPS = 1_000_000;

    private @Nullable TestInfo testInfo;

    private TestInfo testInfo() {
        return Objects.requireNonNull(testInfo);
    }

    @BeforeEach
    void init(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    protected String readFile(String filename) throws IOException {
        Path path = Path.of(getScriptsDirectory(), filename);
        return Files.readString(path);
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile()
                .setDebugLevel(3)
                // Messes out code coverage otherwise
                .setSignature(false)
                // Do not remove end instructions
                .setOptimizationLevel(Optimization.JUMP_THREADING, OptimizationLevel.BASIC)
                // Do not merge constants in print statements
                .setOptimizationLevel(Optimization.PRINT_MERGING, OptimizationLevel.BASIC)
                .setExecutionFlag(ExecutionFlag.DUMP_VARIABLES_ON_STOP, false);
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
        data.add(LogicInstructionPrinter.toString(compiler.instructionProcessor(), instructions));
        String joined = String.join("\n", data)
                .replaceAll("\\R", System.lineSeparator());

        try {
            Files.writeString(logFile, joined);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void compileAndOutputCode(String title, String code, Path logFile) {
        InputFiles inputFiles = InputFiles.fromSource(code);
        MindcodeCompiler compiler = new MindcodeCompiler(expectedMessages(), createCompilerProfile(), inputFiles);
        compiler.compile();
        logCompilation(title, code, compiler.getOutput(), compiler.getInstructions().size());
        writeLogFile(logFile, compiler, compiler.getUnresolved());
    }

    protected void compileAndOutputFile(String fileName) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        compileAndOutputCode(fileName, readFile(fileName), logFile);
    }


    protected void setupEmulator(Processor emulator, Map<String, MindustryBlock> blocks) {
        emulator.addBlock("bank1", Memory.createMemoryBank());
        emulator.addBlock("bank2", Memory.createMemoryBank());
        blocks.forEach(emulator::addBlock);
    }

    protected void testAndEvaluateCode(@Nullable String title, ExpectedMessages expectedMessages, String code,
            Map<String, MindustryBlock> blocks, RunEvaluator evaluator, @Nullable Path logFile) {
        process(expectedMessages,
                InputFiles.fromSource(code),
                emulator -> setupEmulator(emulator, blocks),
                compiler -> {
                    assertFalse(compiler.hasErrors(), "Errors while compiling program.");
                    writeLogFile(logFile, compiler, compiler.getUnresolved());
                    logPerformance(title, code, compiler.getOutput(), compiler.getEmulator());

                    List<Executable> executables = new ArrayList<>();
                    executables.add(() -> evaluator.asExpected(true, compiler.getEmulator()));
                    compiler.getAssertions().forEach(a -> executables.add(() -> assertTrue(a.success(), a.generateErrorMessage())));
                    executables.add(() -> assertNull(compiler.getExecutionException()));
                    assertAll(executables);

                    return null;
                });
    }

    protected RunEvaluator createOutputEvaluator(MindcodeCompiler compiler, String expectedOutput) {
        return (useAsserts, processor) -> {
            String actualOutput = processor.getTextBuffer().getFormattedOutput();
            boolean matches = Objects.equals(expectedOutput, actualOutput);
            if (useAsserts) {
                assertEquals(expectedOutput, actualOutput,
                        () -> compiler.getMessages().stream().map(MindcodeMessage::message)
                                .collect(Collectors.joining("\n", "\n", "\n")));
            }
            return matches;
        };
    }

    protected RunEvaluator outputEvaluator(List<String> expectedOutput) {
        return (useAsserts, compiler) -> {
            List<String> actualOutput = compiler.getTextBuffer().getPrintOutput();
            boolean outputMatches = Objects.equals(expectedOutput, actualOutput);
            if (useAsserts) {
                assertEquals(expectedOutput, actualOutput);
            }

            boolean assertionsMatch = compiler.getAssertions().stream().allMatch(Assertion::success);
            if (useAsserts && !assertionsMatch) {
                compiler.getAssertions().stream().filter(Assertion::failure).forEach(a -> fail(a.generateErrorMessage()));
            }
            return outputMatches && assertionsMatch;
        };
    }

    protected RunEvaluator assertEvaluator() {
        return (useAsserts, processor) -> {
            if (useAsserts) {
                if (processor.getAssertions().isEmpty()) {
                    fail("Expected assertions not present");
                }
            }
            return !processor.getAssertions().isEmpty();
        };
    }

    protected void testAndEvaluateFile(String fileName, Map<String, MindustryBlock> blocks,
            ExpectedMessages expectedMessages, RunEvaluator evaluator) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(fileName, expectedMessages, readFile(fileName), blocks, evaluator, logFile);
    }

    protected void testAndEvaluateFile(String fileName, Function<String, String> codeDecorator,
            Map<String, MindustryBlock> blocks, RunEvaluator evaluator) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(fileName, expectedMessages(), codeDecorator.apply(readFile(fileName)), blocks, evaluator, logFile);
    }

    protected void testAndEvaluateFile(String fileName, Function<String, String> codeDecorator,
            Map<String, MindustryBlock> blocks, List<String> expectedOutputs) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(fileName, expectedMessages(), codeDecorator.apply(readFile(fileName)), blocks,
                outputEvaluator(expectedOutputs), logFile);
    }

    protected void testAndEvaluateFile(String fileName, List<String> expectedOutputs) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(fileName, expectedMessages(), readFile(fileName), Map.of(),
                outputEvaluator(expectedOutputs), logFile);
    }

    protected void testAndEvaluateFile(String fileName) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(fileName, expectedMessages(), readFile(fileName), Map.of(), assertEvaluator(), logFile);
    }

    protected void testCode(ExpectedMessages expectedMessages, String code, Map<String, MindustryBlock> blocks, String... expectedOutputs) {
        testAndEvaluateCode(null, expectedMessages, code, blocks, outputEvaluator(List.of(expectedOutputs)), null);
    }

    protected void testCode(ExpectedMessages expectedMessages, String code, String... expectedOutputs) {
        testAndEvaluateCode(null, expectedMessages, code, Map.of(), outputEvaluator(List.of(expectedOutputs)), null);
    }

    protected void testCode(String code, Map<String, MindustryBlock> blocks, String... expectedOutputs) {
        testAndEvaluateCode(null, expectedMessages(), code, blocks, outputEvaluator(List.of(expectedOutputs)), null);
    }

    protected void testCode(String code, String... expectedOutputs) {
        testAndEvaluateCode(null, expectedMessages(), code, Map.of(), outputEvaluator(List.of(expectedOutputs)), null);
    }

    protected void testAssertions(String code) {
        testAndEvaluateCode(null, expectedMessages(), code, Map.of(), assertEvaluator(), null);
    }
}
