package info.teksol.mindcode.processor;

import info.teksol.mindcode.compiler.*;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Base class for algorithm tests
// Processor for execution is equipped with bank1 memory bank.
// Additional blocks can be added
public abstract class AbstractProcessorTest extends AbstractOptimizerTest<Optimizer> {

    protected abstract String getScriptsDirectory();

    private static final List<String> performance = new ArrayList<>();
    private static final List<String> timing = new ArrayList<>();

    static void init() {
        performance.clear();
        timing.clear();
    }

    static void done(String scriptsDirectory, String className) throws IOException {
        Path path = Path.of(scriptsDirectory, className + ".txt");
        Collections.sort(performance);
        Files.write(path, performance);

        Path path2 = Path.of(scriptsDirectory, className + "_timing.txt");
        Collections.sort(timing);
        Files.write(path2, timing);
    }

    /** Nearest multiple of millisecond to round timing to */
    private static final int PRECISION = 200;

    private void logTiming(String title, List<CompilerMessage> messages) {
        String name = title != null ? title : testInfo.getDisplayName().replaceAll("\\(\\)", "");
        String timings = messages.stream()
                .filter(TimingMessage.class::isInstance)
                .map(TimingMessage.class::cast)
                .map(m -> String.format(Locale.US, "%s: %,10d ms", m.phase(),
                        PRECISION * ((PRECISION / 2 + m.milliseconds()) / PRECISION)))
                .collect(Collectors.joining(", "));

        String text = String.format("%-40s: %s", name + ":", timings.toLowerCase());
        timing.add(text);
    }

    private void logCompilation(String title, String code, String compiled, int instructions) {
        String name = title != null ? title : testInfo.getDisplayName().replaceAll("\\(\\)", "");
        String info = String.format(Locale.US,
                "%-40s %4d instructions, source CRC %016X, compiled CRC %016X",
                name + ":", instructions,
                CRC64.hash1(code.getBytes(StandardCharsets.UTF_8)),
                CRC64.hash1(compiled.getBytes(StandardCharsets.UTF_8)));
        System.out.println(info);
        performance.add(info);
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
        performance.add(info);
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
    protected final int MAX_STEPS = 1000000;

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
        profile.setAllOptimizationLevels(OptimizationLevel.AGGRESSIVE);
        profile.setDebugLevel(3);
        // Do not remove end instructions
        profile.setOptimizationLevel(Optimization.JUMP_TARGET_PROPAGATION, OptimizationLevel.BASIC);
        // Do not merge constants in print statements
        profile.setOptimizationLevel(Optimization.PRINT_TEXT_MERGING, OptimizationLevel.BASIC);
        return profile;
    }

    protected DebugPrinter createDebugPrinter() {
        return new DiffDebugPrinter(3);
    }

    private void writeLogFile(Path logFile, TestCompiler compiler, List<LogicInstruction> instructions) {
        if (logFile == null) {
            return;
        }

        List<String> data = compiler.getMessages().stream()
                .filter(m -> !(m instanceof TimingMessage))
                .map(CompilerMessage::message)
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
        List<LogicInstruction> unresolved = generateInstructions(compiler, code).instructions();
        assertNoUnexpectedMessages(compiler, s -> false);
        List<LogicInstruction> instructions = LogicInstructionLabelResolver.resolve(compiler.processor, compiler.profile, unresolved);
        String compiled = LogicInstructionPrinter.toString(compiler.processor, instructions);
        logTiming(title, compiler.getMessages());
        logCompilation(title, code, compiled, instructions.size());
        writeLogFile(logFile, compiler, unresolved);
    }

    protected void compileAndOutputFile(String fileName) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        compileAndOutputCode(createTestCompiler(), fileName, readFile(fileName), logFile);
    }

    protected void testAndEvaluateCode(TestCompiler compiler, String title, String code, List<MindustryObject> blocks,
            Consumer<List<String>> evaluator, Path logFile) {
        Processor processor = new Processor();
        processor.addBlock(MindustryMemory.createMemoryBank("bank1"));
        processor.addBlock(MindustryMemory.createMemoryBank("bank2"));
        blocks.forEach(processor::addBlock);
        List<LogicInstruction> unresolved = generateInstructions(compiler, code).instructions();
        List<LogicInstruction> instructions = LogicInstructionLabelResolver.resolve(compiler.processor, compiler.profile, unresolved);
        writeLogFile(logFile, compiler, unresolved);
        //System.out.println(prettyPrint(instructions));
        processor.run(instructions, MAX_STEPS);
        String compiled = LogicInstructionPrinter.toString(compiler.processor, instructions);
        logTiming(title, compiler.getMessages());
        logPerformance(title, code, compiled, processor);
        //System.out.println(String.join("", processor.getTextBuffer()));

        assertAll(
                () -> evaluator.accept(processor.getTextBuffer()),
                () -> assertNoUnexpectedMessages(compiler, s -> false)
        );
    }

    protected Consumer<List<String>> createEvaluator(TestCompiler compiler, List<String> expectedOutput) {
        return (List<String> outputs) -> assertEquals(expectedOutput, outputs,
                () -> compiler.getMessages().stream().map(CompilerMessage::message)
                        .collect(Collectors.joining("\n", "\n", "\n")));
    }

    protected void testAndEvaluateFile(TestCompiler compiler, String fileName, Function<String, String> codeDecorator,
            List<MindustryObject> blocks, Consumer<List<String>> evaluator) throws IOException {
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(compiler, fileName, codeDecorator.apply(readFile(fileName)),
                blocks, evaluator, logFile);
    }

    protected void testAndEvaluateFile(String fileName, Function<String, String> codeDecorator,
            List<MindustryObject> blocks, List<String> expectedOutputs) throws IOException {
        TestCompiler compiler = createTestCompiler();
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(createTestCompiler(), fileName, codeDecorator.apply(readFile(fileName)),
                blocks, createEvaluator(compiler, expectedOutputs), logFile);
    }

    protected void testAndEvaluateFile(String fileName, List<String> expectedOutputs) throws IOException {
        TestCompiler compiler = createTestCompiler();
        Path logFile = Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log");
        testAndEvaluateCode(createTestCompiler(), fileName, readFile(fileName), List.of(),
                createEvaluator(compiler, expectedOutputs), logFile);
    }

    protected void testCode(String code, List<MindustryObject> blocks, List<String> expectedOutputs) {
        TestCompiler compiler = createTestCompiler();
        testAndEvaluateCode(compiler, null, code, blocks, createEvaluator(compiler, expectedOutputs), null);
    }

    protected void testCode(String code, String... expectedOutputs) {
        testCode(code, List.of(), List.of(expectedOutputs));
    }
}
