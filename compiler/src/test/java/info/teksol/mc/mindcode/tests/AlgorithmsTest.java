package info.teksol.mc.mindcode.tests;

import info.teksol.mc.emulator.blocks.Memory;
import info.teksol.mc.emulator.blocks.graphics.LogicDisplay;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@NullMarked
@Order(5)
public class AlgorithmsTest extends AbstractProcessorTest {

    public static final String SCRIPTS_DIRECTORY = SCRIPTS_BASE_DIRECTORY + "/algorithms";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, AlgorithmsTest.class.getSimpleName());
    }

    @Test
    void storageDisplayTest() throws IOException {
        testAndEvaluateFile("storage-display.mnd",
                s -> "AMOUNT = 12345;\n" + s,
                Map.of("display1", LogicDisplay.createLargeLogicDisplay()),
                List.of()
        );
    }

    void executeSortingAlgorithmTest(String fileName, int arrayLength) throws IOException {
        Random rnd = new Random(0);
        double[] array = rnd.ints().mapToDouble(i -> Math.abs(i) % 1000).limit(arrayLength).toArray();
        double[] sorted = Arrays.copyOf(array, array.length);
        Arrays.sort(sorted);

        testAndEvaluateCode(
                "sorting with " + fileName,
                expectedMessages(),
                "param SIZE = " + arrayLength + ";\n" + readFile(fileName),
                Map.of(
                        "bank2", Memory.createMemoryBank(array),
                        "bank3", Memory.createMemoryBank(sorted)
                ),
                assertEvaluator(),
                Path.of(getScriptsDirectory(), fileName.replace(".mnd", "") + ".log")
        );
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode sortsArrays() {
        Map<String, Integer> definitions = Map.of(
                "bubble-sort.mnd", 64,
                "heap-sort.mnd", 512,
                "insert-sort.mnd", 128,
                "quick-sort.mnd", 512,
                "select-sort.mnd", 128
        );

        return DynamicContainer.dynamicContainer("Array sorting tests",
                definitions.keySet().stream().map(name -> DynamicTest.dynamicTest(name, null,
                        () -> executeSortingAlgorithmTest(name, definitions.get(name))))
        );
    }


    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode computesScriptTests() {
        final List<DynamicTest> result = new ArrayList<>();
        final List<String> fileNames = List.of(
                "array-reversion.mnd",
                "compute-recursive-fibonacci.mnd",
                "compute-sum-of-primes.mnd",
                "memory-read-write.mnd"
        );

        return DynamicContainer.dynamicContainer("Script tests",
                fileNames.stream().map(name -> DynamicTest.dynamicTest(name, null,
                        () -> testAndEvaluateFile(name))
                )
        );
    }

    //@TestFactory
    public List<DynamicTest> runDrawingTests() throws IOException {
        List<Optimization> optimizations = List.of(
                Optimization.UNREACHABLE_CODE_ELIMINATION,
                Optimization.PRINT_MERGING,
                Optimization.RETURN_OPTIMIZATION,
                Optimization.STACK_OPTIMIZATION,
                Optimization.EXPRESSION_OPTIMIZATION,
                Optimization.JUMP_NORMALIZATION,
                Optimization.JUMP_OPTIMIZATION,
                Optimization.JUMP_STRAIGHTENING,
                Optimization.CASE_EXPRESSION_OPTIMIZATION,
                Optimization.IF_EXPRESSION_OPTIMIZATION,
                Optimization.JUMP_THREADING,
                Optimization.TEMP_VARIABLES_ELIMINATION,
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.SINGLE_STEP_ELIMINATION,
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
                    compilerProfile.setOptimizationLevel(optimizations.get(i), OptimizationLevel.EXPERIMENTAL);
                }
                processDrawingCode(result, index == 0 ? "None" : "+ " + optimizations.get(index - 1).getName(),
                        compilerProfile, code, number);
            }
        }
        return result;
    }

    private void processDrawingCode(List<DynamicTest> result, String name, CompilerProfile profile, String code, int number) {
        result.add(DynamicTest.dynamicTest(name, null, () -> testAndEvaluateCode(
                number + ", " + name,
                expectedMessages(),
                "AMOUNT = " + number + "\n" + code,
                Map.of(),
                (useAsserts, expectedOutput) -> true,
                Path.of(getScriptsDirectory(), "storage-display.log")
        )));
    }
}
