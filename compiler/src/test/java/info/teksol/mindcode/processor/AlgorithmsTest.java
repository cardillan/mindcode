package info.teksol.mindcode.processor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class AlgorithmsTest extends AbstractProcessorTest {

    @Test
    void computesRecursiveFibonacci() {
        testCode(""
                + "allocate stack in bank1[0...512] "
                + "def fib(n) "
                + "    n < 2 ? n : fib(n - 1) + fib(n - 2) "
                + "end "
                + "print(fib(10)) "
                + "stopProcessor()",
                "55"
        );
    }

    @Test
    void executeBitTest() throws IOException {
        testFile(
                "bitmap-get-set-test.mnd",
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

        String code = "SIZE = " + arrayLength + "\n" + readFile(fileName);
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
}
