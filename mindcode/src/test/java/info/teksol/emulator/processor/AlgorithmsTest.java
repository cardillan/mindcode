package info.teksol.emulator.processor;

import info.teksol.emulator.blocks.Memory;
import info.teksol.emulator.blocks.graphics.LogicDisplay;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AlgorithmsTest extends AbstractProcessorTest {

    public static final String SCRIPTS_DIRECTORY = "src/test/resources/info/teksol/emulator/processor/algorithms";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @BeforeAll
    static void init() {
        AbstractProcessorTest.init();
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, AlgorithmsTest.class.getSimpleName());
    }

    @Test
    void memoryBitReadTest() throws IOException {
        testAndEvaluateFile("bitmap-get.mnd",
                IntStream.range(0, 16).map(i -> i % 2).mapToObj(String::valueOf).collect(Collectors.toList())
        );
    }

    @Test
    void memoryBitReadWriteTest() throws IOException {
        testAndEvaluateFile("bitmap-get-set.mnd",
                IntStream.range(1, 17).map(i -> i % 2).mapToObj(String::valueOf).collect(Collectors.toList())
        );
    }


    @Test
    void storageDisplayTest() throws IOException {
        testAndEvaluateFile("storage-display.mnd",
                s -> "AMOUNT = 12345\n" + s,
                Map.of("display1", LogicDisplay.createLargeLogicDisplay()),
                List.of()
        );
    }

    void executeSortingAlgorithmTest(String fileName, int arrayLength) throws IOException {
        TestCompiler compiler = createTestCompiler();
        Random rnd = new Random(0);
        double[] array = rnd.ints().mapToDouble(i -> Math.abs(i) % 1000).limit(arrayLength).toArray();
        List<String> expectedOutput = Arrays.stream(array).mapToInt(d -> (int) d)
                .sorted().mapToObj(String::valueOf).toList();

        testAndEvaluateCode(
                compiler,
                "sorting with " + fileName,
                "param SIZE = " + arrayLength + "\n" + readFile(fileName),
                Map.of("bank2", Memory.createMemoryBank(array)),
                createEvaluator(compiler, expectedOutput),
                Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log")
        );
    }

    @TestFactory
    public List<DynamicTest> sortsArrays() {
        final List<DynamicTest> result = new ArrayList<>();
        Map<String, Integer> definitions = Map.of(
                "bubble-sort.mnd", 64,
                "heap-sort.mnd", 512,
                "insert-sort.mnd", 128,
                "quick-sort.mnd", 512,
                "select-sort.mnd", 128
        );

        for (final String script : definitions.keySet()) {
            result.add(DynamicTest.dynamicTest(script, null,
                    () -> executeSortingAlgorithmTest(script, definitions.get(script))));
        }

        return result;
    }

    @TestFactory
    public List<DynamicTest> computesScriptTests() {
        final List<DynamicTest> result = new ArrayList<>();
        final List<String> definitions = List.of(
                "memory-read-write.mnd", "10",
                "compute-recursive-fibonacci.mnd", "55",
                "compute-sum-of-primes.mnd", "21536"
        );

        for (int i = 0; i < definitions.size(); i += 2) {
            processFile(result, definitions.get(i), definitions.get(i + 1));
        }

        return result;
    }

    private void processFile(List<DynamicTest> result, String fileName, String expectedOutput) {
        result.add(DynamicTest.dynamicTest(fileName, null, () -> testAndEvaluateFile(
                fileName,
                s -> s,
                Map.of("bank2", Memory.createMemoryBank()),
                List.of(expectedOutput)
        )));
    }

    //@TestFactory
    public List<DynamicTest> runDrawingTests() throws IOException {
        List<Optimization> optimizations = List.of(
                Optimization.UNREACHABLE_CODE_ELIMINATION,
                Optimization.PRINT_TEXT_MERGING,
                Optimization.RETURN_OPTIMIZATION,
                Optimization.STACK_USAGE_OPTIMIZATION,
                Optimization.EXPRESSION_OPTIMIZATION,
                Optimization.CONDITIONAL_JUMPS_NORMALIZATION,
                Optimization.CONDITIONAL_JUMPS_OPTIMIZATION,
                Optimization.JUMP_OVER_JUMP_ELIMINATION,
                Optimization.CASE_EXPRESSION_OPTIMIZATION,
                Optimization.IF_EXPRESSION_OPTIMIZATION,
                Optimization.JUMP_TARGET_PROPAGATION,
                Optimization.TEMP_VARIABLES_ELIMINATION,
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.SINGLE_STEP_JUMP_ELIMINATION,
                Optimization.LOOP_OPTIMIZATION,
                Optimization.LOOP_HOISTING,
                Optimization.DATA_FLOW_OPTIMIZATION,
                Optimization.FUNCTION_INLINING,
                Optimization.LOOP_UNROLLING,
                Optimization.CASE_SWITCHING
        );

        final List<DynamicTest> result = new ArrayList<>();
        int[] numbers = { 21600, 12345, 13579 };
        String code = readFile("storage-display.mnd");

        for (int number : numbers) {
            for (int index = 0; index <= optimizations.size(); index++) {
                CompilerProfile compilerProfile = createCompilerProfile()
                        .setAllOptimizationLevels(OptimizationLevel.NONE)
                        .setInstructionLimit(1100);
                for (int i = 0; i < index; i++) {
                    compilerProfile.setOptimizationLevel(optimizations.get(i), OptimizationLevel.ADVANCED);
                }
                processDrawingCode(result, index == 0 ? "None" : "+ " + optimizations.get(index - 1).getName(),
                        compilerProfile, code, number);
            }
        }
        return result;
    }

    private void processDrawingCode(List<DynamicTest> result, String name, CompilerProfile profile, String code, int number) {
        result.add(DynamicTest.dynamicTest(name, null, () -> testAndEvaluateCode(
                createTestCompiler(profile),
                number + ", " + name,
                "AMOUNT = " + number + "\n" + code,
                Map.of(),
                (useAsserts, expectedOutput) -> true,
                Path.of(getScriptsDirectory(), "storage-display.log")
        )));
    }
}
