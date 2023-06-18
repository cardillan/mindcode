package info.teksol.mindcode.processor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AlgorithmsTest extends AbstractProcessorTest {

    @BeforeAll
    static void init() {
        AbstractProcessorTest.init();
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(AlgorithmsTest.class.getSimpleName());
    }

    @Test
    void memoryBitReadTest() throws IOException {
        testFile("bitmap-get.mnd",
                List.of(),
                IntStream.range(0, 16).map(i -> i % 2).mapToObj(String::valueOf).collect(Collectors.toList())
        );
    }

    @Test
    void memoryBitReadWriteTest() throws IOException {
        testFile("bitmap-get-set.mnd",
                List.of(),
                IntStream.range(1, 17).map(i -> i % 2).mapToObj(String::valueOf).collect(Collectors.toList())
        );
    }

    void executeSortingAlgorithmTest(String fileName, int arrayLength) throws IOException {
        Random rnd = new Random(0);
        double[] array = rnd.ints().mapToDouble(i -> Math.abs(i) % 1000).limit(arrayLength).toArray();
        MindustryMemory memory = MindustryMemory.createMemoryBank("bank2", array);

        String code = "const SIZE = " + arrayLength + "\n" + readFile(fileName);
        testCode("sorting with " + fileName,
                code,
                List.of(memory),
                Arrays.stream(array).mapToInt(d -> (int) d).sorted().mapToObj(String::valueOf).collect(Collectors.toList())
        );
    }

    @TestFactory
    public List<DynamicTest> sortsArrays() {
        final List<DynamicTest> result = new ArrayList<>();
        Map<String, Integer> definitions = Map.of(
                 "bubble-sort.mnd", 64,
                 "heap-sort.mnd",   512,
                 "insert-sort.mnd", 128,
                 "quick-sort.mnd",  512,
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
                 "memory-read-write.mnd",           "10",
                 "compute-recursive-fibonacci.mnd", "55",
                 "compute-sum-of-primes.mnd",       "21536",
                 "project-euler-04.mnd",            "9009",
                 "project-euler-18.mnd",            "1074",
                 "project-euler-26.mnd",            "97",
                 "project-euler-28.mnd",            "669171001",
                 "project-euler-31.mnd",            "41",
                 "project-euler-31b.mnd",           "73682",
                 "project-euler-45.mnd",            "1533776805",
                 "project-euler-97.mnd",            "7075090433"
        );

        for (int i = 0; i < definitions.size(); i += 2) {
            String fileName = definitions.get(i);
            List<String> expectedOutput = List.of(definitions.get(i + 1));
            List<MindustryObject> memory = List.of(MindustryMemory.createMemoryBank("bank2"));
            result.add(DynamicTest.dynamicTest(fileName, null,
                    () -> testFile(fileName, memory, expectedOutput)));
        }

        return result;
    }
}
