package info.teksol.mindcode.processor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Some Mindcode code tested in this class can be quite complex and hard to maintain as a string constant.
// Such code can be saved as a file in the src/test/resources/scripts directory.
public class ProcessorTest extends AbstractProcessorTest {

    @BeforeAll
    static void init() {
        AbstractProcessorTest.init();
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(ProcessorTest.class.getSimpleName());
    }

    @Test
    void printsNumbersStringsObjects() {
        testCode("""
                        print("Hello", 10, 5.5, bank1, null, true, false)
                        """,
                "Hello", "10", "5.5", "bank", "null", "1", "0"
        );
    }

    @Test
    void computesPackColor() {
        testCode("""
                        c = packcolor(1.0, 1.0, 1.0, 1.0)
                        print(c)
                        """,
                "0"
        );
    }

    @Test
    void executesBasicOperations() throws IOException {
        testAndEvaluateFile("basic-operations.mnd",
                List.of(),
                output -> {
                    assertTrue(output.size() > 10, "Test didn't produce enough output values (expected more than 10, got " + output.size() + ".");
                    String messages = output.stream().filter(s -> !"ok".equals(s)).collect(Collectors.joining());
                    System.out.print(messages);
                    assertTrue(messages.isEmpty(), "Some of the tests have failed. See output for details.");
                }
        );
    }

    @Test
    void executesBasicEvaluations() throws IOException {
        testAndEvaluateFile("basic-evaluations.mnd",
                List.of(),
                output -> {
                    assertTrue(output.size() > 10, "Test didn't produce enough output values (expected more than 10, got " + output.size() + ".");
                    String messages = output.stream().filter(s -> !"ok".equals(s)).collect(Collectors.joining());
                    System.out.print(messages);
                    assertTrue(messages.isEmpty(), "Some of the tests have failed. See output for details.");
                }
        );
    }

    @Test
    void handlesAssignmentsInConditions() {
        testCode("""
                        if res = 2 < 3
                            print(true)
                        end
                        print(res)
                        """,
                "1", "1"
        );
    }

    @Test
    void executesWhileLoop() {
        List<String> expected = new ArrayList<>();
        for (int i = 0, j = 0; i < 10; ) {
            expected.add(String.valueOf(j += i++));
        }

        testCode("""
                        allocate stack in bank1[0...512]
                        def d(n) n > 0 ? -d(-n) : n end
                        i = d(j = d(0))
                        while i < 10
                            print(j += i)
                            i += 1
                        end
                        """,
                List.of(),
                expected
        );
    }

    @Test
    void executesForEachLoopBreakContinue() {
        testCode("""
                        allocate stack in bank1[0...512]
                        def d(n) n > 0 ? -d(-n) : n end
                        for i in (1, d(2), 3, d(5), 8, d(12), 15)
                            if d(i) == 3 continue end
                            print(i)
                            if d(i) == 12 break end
                        end
                        """,
                "1", "2", "5", "8", "12"
        );
    }

    @Test
    void executesRangedForLoopBreakContinue() {
        testCode("""
                        allocate stack in bank1[0...512]
                        def d(n) n > 0 ? -d(-n) : n end
                        for i in d(1) ... d(10)
                            if i == 3 continue end
                            print(i)
                            if i == d(5) break end
                        end
                        """,
                "1", "2", "4", "5"
        );
    }

    @Test
    void executesIteratedForLoopBreakContinue() {
        testCode("""
                        allocate stack in bank1[0...512]
                        def d(n) n > 0 ? -d(-n) : n end
                        for i = 0, j = 10; d(i) <= d(j); i += 2, j += 1
                            if d(i) == 4 continue end
                            print(i, j)
                            if d(i) == 10 break end
                        end
                        """,
                "0", "10", "2", "11", "6", "13", "8", "14", "10", "15"
        );
    }

    @Test
    void executesComplexCaseExpressions() {
        testCode("""
                        allocate stack in bank1[0...512]
                        def d(n) n > 0 ? -d(-n) : n end
                        for i in d(1) .. d(10)
                            str = case i
                                when d(1) then "A"
                                when d(2), d(3), 4 then "B"
                                when 5 then continue
                                when d(6) .. d(8) then "C"
                                when 10 then break
                                else "D"
                            end
                            print(str)
                        end
                        """,
                "A", "B", "B", "B", "C", "C", "C", "D"
        );
    }

    @Test
    void executesFixedBoundsRangedFor() {
        testCode("""
                        a = 0
                        b = 10
                        for i in a ... b
                            b = b - 1
                        end
                        print(i)
                        """,
                "10"
        );
    }

    @Test
    void executesLoopsInConditions() {
        testCode("""
                        inline def sum(n)
                            c = 0
                            for i in 0 ... n
                                c += i
                            end
                            print(c)
                            return c
                        end

                        result = if sum(4) < sum(8)
                            print("Less")
                            0
                        else
                            1
                        end
                        print(result)
                        """,
                "6", "28", "Less", "0"
        );
    }

    @Test
    void executesExpressionWithAssignments() {
        // The preferred output might be "3", "4": see #96
        testCode("""
                        a = 1
                        print(a + (a += 1))
                        b = 1
                        print((b += 1) + b)
                        """,
                "4", "4"
        );
    }
}
