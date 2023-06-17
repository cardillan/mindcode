package info.teksol.mindcode.processor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
    void readsAndWritesMemory() {
        testCode("""
                        allocate heap in bank1[0...512]
                        $A = 10
                        print($A)
                        """,
                "10"
        );
    }

    @Test
    void computesRecursiveFibonacci() {
        testCode("""
                        allocate stack in bank1[0...512]
                        def fib(n)
                            n < 2 ? n : fib(n - 1) + fib(n - 2)
                        end
                        print(fib(10))
                        """,
                "55"
        );
    }

    @Test
    void executeBitReadTest() throws IOException {
        testFile("bit-read-test.mnd",
                List.of(),
                IntStream.range(0, 16).map(i -> i % 2).mapToObj(String::valueOf).collect(Collectors.toList())
        );
    }

    @Test
    void executeBitGetSetTest() throws IOException {
        testFile("bitmap-get-set-test.mnd",
                List.of(),
                IntStream.range(1, 17).map(i -> i % 2).mapToObj(String::valueOf).collect(Collectors.toList())
        );
    }

    @Test
    void computesSumOfPrimes() throws IOException {
        testFile("compute-sum-of-primes.mnd",
                "21536"
        );
    }

    void executeSortingAlgorithmTest(String fileName, int arrayLength) throws IOException {
        Random rnd = new Random(0);
        double[] array = rnd.ints().mapToDouble(i -> Math.abs(i) % 1000).limit(arrayLength).toArray();
        MindustryMemory memory = MindustryMemory.createMemoryBank("bank2", array);

        String code = "const SIZE = " + arrayLength + "\n" + readFile(fileName);
        testCode(code,
                List.of(memory),
                Arrays.stream(array).mapToInt(d -> (int) d).sorted().mapToObj(String::valueOf).collect(Collectors.toList())
        );
    }

    @Test
    void executeBubblesortTest() throws IOException {
        executeSortingAlgorithmTest("bubblesort.mnd", 64);
    }

    @Test
    void executeInsertsortTest() throws IOException {
        executeSortingAlgorithmTest("insertsort.mnd", 128);
    }

    @Test
    void executeSelectsortTest() throws IOException {
        executeSortingAlgorithmTest("selectsort.mnd", 128);
    }

    @Test
    void executeHeapsortTest() throws IOException {
        executeSortingAlgorithmTest("heapsort.mnd", 512);
    }

    @Test
    void executeQuicksortTest() throws IOException {
        executeSortingAlgorithmTest("quicksort.mnd", 512);
    }

    @Test
    void computesProjectEuler04() throws IOException {
        testFile("project-euler-04.mnd", "9009");
    }

    @Test
    void computesProjectEuler18() throws IOException {
        testFile("project-euler-18.mnd", "1074");
    }

    @Test
    void computesProjectEuler26() throws IOException {
        testFile("project-euler-26.mnd", "97");
    }

    @Test
    void computesProjectEuler28() throws IOException {
        testFile("project-euler-28.mnd", "669171001");
    }

    @Test
    void computesProjectEuler31() throws IOException {
        testFile("project-euler-31.mnd", "41");
    }

    @Test
    void computesProjectEuler45() throws IOException {
        testFile("project-euler-45.mnd", "1533776805");
    }
}
