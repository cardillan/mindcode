package info.teksol.emulator.processor;

import info.teksol.emulator.blocks.Memory;
import info.teksol.emulator.blocks.MindustryBlock;
import info.teksol.mindcode.MessageLevel;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.LogicInstructionLabelResolver;
import info.teksol.mindcode.compiler.LogicInstructionPrinter;
import info.teksol.mindcode.compiler.OptimizerTimingMessage;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.*;
import info.teksol.util.ExpectedMessages;
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
public abstract class AbstractProcessorTest extends AbstractOptimizerTest<Optimizer> {

    protected abstract String getScriptsDirectory();

    private static final Map<String, String> headers = new ConcurrentHashMap<>();
    private static final Map<String, Queue<String>> results = new ConcurrentHashMap<>();

    // Lambda interface
    // Handles evaluating of expected vs. actual program output
    protected interface RunEvaluator {
        /**
         * Called to compare the actual results of running a code with an expected state.
         * @param useAsserts if true, the evaluator should explicitly assert the equality, otherwise it just reports
         *                   the result. Each evaluator will be called at lest once with useAsserts set to true.
         * @param processor  processor containing the results of the test run
         * @return true if the actual results output matches the expected one
         */
        boolean asExpected(boolean useAsserts, Processor processor);
    }

    static void done(String scriptsDirectory, String className) throws IOException {
        Path path = Path.of(scriptsDirectory, className + ".txt");
        String[] array = results.get(className).toArray(new String[0]);
        List<String> texts = Stream.of(array).sorted().collect(Collectors.toCollection(ArrayList::new));
        texts.add(0, headers.get(className));
        Files.write(path, texts);
    }

    private void logCompilation(String title, String code, String compiled, int instructions) {
        String name = title != null ? title : testInfo.getDisplayName().replaceAll("\\(\\)", "");
        headers.computeIfAbsent(getClass().getSimpleName(), k ->
                String.format("%-40s %11s   %12s   %-16s   %-16s", "Name", "Ambiguities", "Instructions", "Source CRC", "Compiled CRC"));
        String info = String.format(Locale.US,
                "%-40s %11d   %12d   %016X   %016X",
                name + ":", parseAmbiguities.get(), instructions,
                CRC64.hash1(code.getBytes(StandardCharsets.UTF_8)),
                CRC64.hash1(compiled.getBytes(StandardCharsets.UTF_8)));
        System.out.println(info);
        results.computeIfAbsent(getClass().getSimpleName(), k -> new ConcurrentLinkedDeque<>()).add(info);
    }

    private void logPerformance(String title, String code, String compiled, Processor processor) {
        int coverage = 1000 * processor.getCoverage().cardinality() / processor.getInstructions();
        String name = title != null ? title : testInfo.getDisplayName().replaceAll("\\(\\)", "");
        headers.computeIfAbsent(getClass().getSimpleName(), k ->
                String.format("%-40s %11s   %12s   %6s   %8s   %-16s   %-16s", "Name", "Ambiguities", "Instructions", "Steps", "Coverage", "Source CRC", "Compiled CRC"));
        String info = String.format(Locale.US,
                "%-40s %11d   %12d   %6d   %5d.%01d%%   %016X   %016X",
                name + ":", parseAmbiguities.get(), processor.getInstructions(), processor.getSteps(), coverage / 10, coverage % 10,
                CRC64.hash1(code.getBytes(StandardCharsets.UTF_8)),
                CRC64.hash1(compiled.getBytes(StandardCharsets.UTF_8)));
        System.out.println(info);
        results.computeIfAbsent(getClass().getSimpleName(), k -> new ConcurrentLinkedDeque<>()).add(info);
    }

    @Override
    protected Class<Optimizer> getTestedClass() {
        return null;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return Optimization.LIST;
    }

    // Prevent unit tests hanging due to possible endless loops in generated code
    protected final int MAX_STEPS = 1_000_000;

    private TestInfo testInfo;

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
        CompilerProfile profile = super.createCompilerProfile();
        profile.setAllOptimizationLevels(OptimizationLevel.EXPERIMENTAL);
        profile.setDebugLevel(3);
        profile.setSignature(false);    // Messes out code coverage otherwise
        // Do not remove end instructions
        profile.setOptimizationLevel(Optimization.JUMP_THREADING, OptimizationLevel.BASIC);
        // Do not merge constants in print statements
        profile.setOptimizationLevel(Optimization.PRINT_MERGING, OptimizationLevel.BASIC);
        return profile;
    }

    protected DebugPrinter getDebugPrinter() {
        return new DiffDebugPrinter(3);
    }

    private void writeLogFile(Path logFile, TestCompiler compiler, List<LogicInstruction> instructions) {
        if (logFile == null) {
            return;
        }

        List<String> data = compiler.getMessages().stream()
                .filter(m -> !(m instanceof OptimizerTimingMessage))
                .map(MindcodeMessage::message)
                .collect(Collectors.toCollection(ArrayList::new));

        data.add("\nFinal code before resolving virtual instructions:\n");
        data.add(LogicInstructionPrinter.toString(compiler.processor, instructions));
        String joined = String.join("\n", data)
                .replaceAll("\\R", System.lineSeparator());

        try {
            Files.writeString(logFile, joined);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void compileAndOutputCode(TestCompiler compiler, String title, String code, Path logFile) {
        List<LogicInstruction> unresolved = generateInstructionsNoMsgValidation(compiler, code).instructions();
        assertNoUnexpectedMessages(compiler, ExpectedMessages.none());
        List<LogicInstruction> instructions = LogicInstructionLabelResolver.resolve(compiler.processor, compiler.profile, unresolved);
        String compiled = LogicInstructionPrinter.toString(compiler.processor, instructions);
        logCompilation(title, code, compiled, instructions.size());
        writeLogFile(logFile, compiler, unresolved);
    }

    protected void compileAndOutputFile(String fileName) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        compileAndOutputCode(createTestCompiler(), fileName, readFile(fileName), logFile);
    }

    protected void testAndEvaluateCode(TestCompiler compiler, String title, String code, Map<String, MindustryBlock> blocks,
            ExpectedMessages expectedMessages, RunEvaluator evaluator, Path logFile) {
        Processor processor = new Processor(expectedMessages, 1000);
        processor.addBlock("bank1", Memory.createMemoryBank());
        processor.addBlock("bank2", Memory.createMemoryBank());
        blocks.forEach(processor::addBlock);
        List<LogicInstruction> unresolved = generateInstructions(compiler, expectedMessages, code).instructions();
        List<LogicInstruction> instructions = LogicInstructionLabelResolver.resolve(compiler.processor, compiler.profile, unresolved);
        writeLogFile(logFile, compiler, unresolved);
        processor.run(instructions, MAX_STEPS);
        String compiled = LogicInstructionPrinter.toString(compiler.processor, instructions);
        logPerformance(title, code, compiled, processor);

        List<Executable> executables = new ArrayList<>();
        executables.add(() -> evaluator.asExpected(true, processor));
        processor.getAssertions().forEach(a -> executables.add(() -> assertTrue(a.success(), a.generateErrorMessage())));
        assertAll(executables);
    }

    protected RunEvaluator createOutputEvaluator(TestCompiler compiler, String expectedOutput) {
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

    protected RunEvaluator outputEvaluator(TestCompiler compiler, List<String> expectedOutput) {
        return (useAsserts, processor) -> {
            List<String> actualOutput = processor.getPrintOutput();
            boolean outputMatches = Objects.equals(expectedOutput, actualOutput);
            if (useAsserts) {
                assertEquals(expectedOutput, actualOutput,
                        () -> compiler.getMessages().stream().map(MindcodeMessage::message)
                                .collect(Collectors.joining("\n", "\n", "\n")));
            }

            boolean assertionsMatch = processor.getAssertions().stream().allMatch(Assertion::success);
            if (useAsserts && !assertionsMatch) {
                processor.getAssertions().stream().filter(Assertion::failure).forEach(a -> fail(a.generateErrorMessage()));
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

    protected ExpectedMessages expectedMessagesInfo() {
        return ExpectedMessages.create().addLevelsUpTo(MessageLevel.INFO).ignored();
    }

    protected void testAndEvaluateFile(TestCompiler compiler, String fileName, Map<String, MindustryBlock> blocks,
            ExpectedMessages expectedMessages, RunEvaluator evaluator) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(compiler, fileName, readFile(fileName), blocks, expectedMessages, evaluator, logFile);
    }

    protected void testAndEvaluateFile(TestCompiler compiler, String fileName, Function<String, String> codeDecorator,
            Map<String, MindustryBlock> blocks, RunEvaluator evaluator) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(compiler, fileName, codeDecorator.apply(readFile(fileName)), blocks,
                ExpectedMessages.none(), evaluator, logFile);
    }

    protected void testAndEvaluateFile(String fileName, Function<String, String> codeDecorator,
            Map<String, MindustryBlock> blocks, List<String> expectedOutputs) throws IOException {
        TestCompiler compiler = createTestCompiler();
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(createTestCompiler(), fileName, codeDecorator.apply(readFile(fileName)), blocks,
                ExpectedMessages.none(), outputEvaluator(compiler, expectedOutputs), logFile);
    }

    protected void testAndEvaluateFile(String fileName, List<String> expectedOutputs) throws IOException {
        TestCompiler compiler = createTestCompiler();
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(createTestCompiler(), fileName, readFile(fileName), Map.of(),
                ExpectedMessages.none(), outputEvaluator(compiler, expectedOutputs), logFile);
    }

    protected void testAndEvaluateFile(String fileName) throws IOException {
        TestCompiler compiler = createTestCompiler();
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(createTestCompiler(),
                fileName,
                readFile(fileName),
                Map.of(),
                expectedMessagesInfo(),
                assertEvaluator(),
                logFile);
    }

    protected void testCode(String code, Map<String, MindustryBlock> blocks, List<String> expectedOutputs) {
        TestCompiler compiler = createTestCompiler();
        testAndEvaluateCode(compiler, null, code, blocks,
                ExpectedMessages.none(), outputEvaluator(compiler, expectedOutputs), null);
    }

    protected void testCode(String code, Map<String, MindustryBlock> blocks, String expectedOutputs) {
        TestCompiler compiler = createTestCompiler();
        testAndEvaluateCode(compiler, null, code, blocks,
                ExpectedMessages.none(), createOutputEvaluator(compiler, expectedOutputs), null);
    }

    protected void testCode(TestCompiler compiler, String code, String... expectedOutputs) {
        testAndEvaluateCode(compiler, "", code, Map.of(), ExpectedMessages.none(),
                outputEvaluator(compiler, List.of(expectedOutputs)), null);
    }

    protected void testCode(String code, String expectedOutputs) {
        testCode(code, Map.of(), expectedOutputs);
    }

    protected void testCode(String code, String... expectedOutputs) {
        testCode(code, Map.of(), List.of(expectedOutputs));
    }
}
