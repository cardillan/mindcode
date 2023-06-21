package info.teksol.mindcode.processor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Some Mindcode code tested in this class can be quite complex and hard to maintain as a string constant.
// Such code can be saved as a file in the src/test/resources/scripts directory.
public class ProcessorTest extends AbstractProcessorTest {

    public static final String SCRIPTS_DIRECTORY = "src/test/resources/processor";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @BeforeAll
    static void init() {
        AbstractProcessorTest.init();
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, ProcessorTest.class.getSimpleName());
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
    void expressionEvaluationRuntime() throws IOException {
        executeMindcodeUnitTests("expression-evaluation-runtime.mnd");
    }

    @Test
    void expressionEvaluationCompileTime() throws IOException {
        executeMindcodeUnitTests("expression-evaluation-compile-time.mnd");
    }

    void executeMindcodeUnitTests(String fileName) throws IOException {
        testAndEvaluateFile(fileName,
                s -> s,
                List.of(),
                output -> {
                    assertFalse(output.isEmpty(), "Test didn't produce any output values.");
                    String messages = output.stream().filter(s -> !"ok".equals(s)).collect(Collectors.joining());
                    System.out.print(messages);
                    assertTrue(messages.isEmpty(), "Some of the tests have failed. See output for details.");
                }
        );
    }

    @Test
    void executesWhileLoop() throws IOException {
        List<String> expected = new ArrayList<>();
        for (int i = 0, j = 0; i < 10; ) {
            expected.add(String.valueOf(j += i++));
        }

        testAndEvaluateFile("while-loop.mnd", expected);
    }

    @Test
    void executesRecursiveFunctionsWithCondition() throws IOException {
        testAndEvaluateFile("recursive-function-condition.mnd",
                List.of("null"));
    }

    void executesForEachLoopBreakContinue() throws IOException {
        testAndEvaluateFile("for-each-loop-break-continue.mnd",
                List.of("1", "2", "5", "8", "12"));
    }

    @Test
    void executesRangedForLoopBreakContinue() throws IOException {
        testAndEvaluateFile("ranged-for-loop-break-continue.mnd",
                List.of("1", "2", "4", "5"));
    }

    @Test
    void executesIteratedForLoopBreakContinue() throws IOException {
        testAndEvaluateFile("iterated-for-loop-break-continue.mnd",
                List.of("0", "10", "2", "11", "6", "13", "8", "14", "10", "15"));
    }

    @Test
    void executesComplexCaseExpressions() throws IOException {
        testAndEvaluateFile("complex-case-expression.mnd",
                List.of("A", "B", "B", "B", "C", "C", "C", "D"));
    }

    @Test
    void executesFixedBoundsRangedFor() throws IOException {
        testAndEvaluateFile("fixed-bounds-ranged-for.mnd",
                List.of("10"));
    }

    @Test
    void executesLoopsInConditions() throws IOException {
        testAndEvaluateFile("loops-in-conditions.mnd",
                List.of("6", "28", "Less", "0"));
    }

    @Test
    void executesExpressionWithAssignments() throws IOException {
        // The preferred output might be "3", "4": see #96
        testAndEvaluateFile("assignments-in-expressions.mnd",
                List.of("4", "4"));
    }
}
