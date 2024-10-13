package info.teksol.emulator.processor;

import info.teksol.emulator.blocks.Memory;
import info.teksol.emulator.blocks.MindustryBlock;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.*;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Base class for algorithm tests
// Processor for execution is equipped with bank1 memory bank.
// Additional blocks can be added
public abstract class AbstractProcessorTest extends AbstractOptimizerTest<Optimizer> {

    protected abstract String getScriptsDirectory();

    private static final Map<String, Queue<String>> results = new ConcurrentHashMap<>();

    // Lambda interface
    // Handles evaluating of expected vs. actual program output
    protected interface OutputEvaluator {
        /**
         * Called to compare the actual output with an (implicit) expected one.
         * @param useAsserts if true, the evaluator should explicitly assert the equality, otherwise it just reports
         *                   the result. Each evaluator will be called at lest once with useAsserts set to true.
         * @param output     the actual output produced by the tested code
         * @return true if the actual output matches the expected one
         */
        boolean compare(boolean useAsserts, List<String> actualOutput);
    }

    static void done(String scriptsDirectory, String className) throws IOException {
        Path path = Path.of(scriptsDirectory, className + ".txt");
        String[] array = results.get(className).toArray(new String[0]);
        List<String> texts = Stream.of(array).sorted().toList();
        Files.write(path, texts);
    }

    private void logCompilation(String title, String code, String compiled, int instructions) {
        String name = title != null ? title : testInfo.getDisplayName().replaceAll("\\(\\)", "");
        String info = String.format(Locale.US,
                "%-40s %4d instructions, source CRC %016X, compiled CRC %016X",
                name + ":", instructions,
                CRC64.hash1(code.getBytes(StandardCharsets.UTF_8)),
                CRC64.hash1(compiled.getBytes(StandardCharsets.UTF_8)));
        System.out.println(info);
        results.computeIfAbsent(getClass().getSimpleName(), k -> new ConcurrentLinkedDeque<>()).add(info);
    }

    private void logPerformance(String title, String code, String compiled, Processor processor) {
        int coverage = 1000 * processor.getCoverage().cardinality() / processor.getInstructions();
        String name = title != null ? title : testInfo.getDisplayName().replaceAll("\\(\\)", "");
        String info = String.format(Locale.US,
                "%-40s %4d instructions, %6d steps, %3d.%01d%% coverage, source CRC %016X, compiled CRC %016X",
                name + ":", processor.getInstructions(), processor.getSteps(), coverage / 10, coverage % 10,
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
        profile.setOptimizationLevel(Optimization.JUMP_TARGET_PROPAGATION, OptimizationLevel.BASIC);
        // Do not merge constants in print statements
        profile.setOptimizationLevel(Optimization.PRINT_TEXT_MERGING, OptimizationLevel.BASIC);
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
                .filter(m -> !(m instanceof TimingMessage))
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
            ExpectedMessages expectedMessages, OutputEvaluator evaluator, Path logFile) {
        Processor processor = new Processor();
        processor.addBlock("bank1", Memory.createMemoryBank());
        processor.addBlock("bank2", Memory.createMemoryBank());
        blocks.forEach(processor::addBlock);
        List<LogicInstruction> unresolved = generateInstructionsNoMsgValidation(compiler, code).instructions();
        List<LogicInstruction> instructions = LogicInstructionLabelResolver.resolve(compiler.processor, compiler.profile, unresolved);
        writeLogFile(logFile, compiler, unresolved);
        processor.run(instructions, MAX_STEPS);
        String compiled = LogicInstructionPrinter.toString(compiler.processor, instructions);
        logPerformance(title, code, compiled, processor);

        assertAll(
                () -> evaluator.compare(true, processor.getPrintOutput()),
                () -> assertNoUnexpectedMessages(compiler, expectedMessages)
        );
    }

    protected OutputEvaluator createEvaluator(TestCompiler compiler, List<String> expectedOutput) {
        return (useAsserts, actualOutput) -> {
            boolean matches = Objects.equals(expectedOutput, actualOutput);
            if (useAsserts) {
                assertEquals(expectedOutput, actualOutput,
                        () -> compiler.getMessages().stream().map(MindcodeMessage::message)
                                .collect(Collectors.joining("\n", "\n", "\n")));
            }
            return matches;
        };
    }

    protected void testAndEvaluateFile(TestCompiler compiler, String fileName, Function<String, String> codeDecorator,
            Map<String, MindustryBlock> blocks, OutputEvaluator evaluator) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(compiler, fileName, codeDecorator.apply(readFile(fileName)), blocks,
                ExpectedMessages.none(), evaluator, logFile);
    }

    protected void testAndEvaluateFile(String fileName, Function<String, String> codeDecorator,
            Map<String, MindustryBlock> blocks, List<String> expectedOutputs) throws IOException {
        TestCompiler compiler = createTestCompiler();
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(createTestCompiler(), fileName, codeDecorator.apply(readFile(fileName)), blocks,
                ExpectedMessages.none(), createEvaluator(compiler, expectedOutputs), logFile);
    }

    protected void testAndEvaluateFile(String fileName, List<String> expectedOutputs) throws IOException {
        TestCompiler compiler = createTestCompiler();
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(createTestCompiler(), fileName, readFile(fileName), Map.of(),
                ExpectedMessages.none(), createEvaluator(compiler, expectedOutputs), logFile);
    }

    protected void testCode(String code, Map<String, MindustryBlock> blocks, List<String> expectedOutputs) {
        TestCompiler compiler = createTestCompiler();
        testAndEvaluateCode(compiler, null, code, blocks,
                ExpectedMessages.none(), createEvaluator(compiler, expectedOutputs), null);
    }

    protected void testCode(String code, String... expectedOutputs) {
        testCode(code, Map.of(), List.of(expectedOutputs));
    }
}
