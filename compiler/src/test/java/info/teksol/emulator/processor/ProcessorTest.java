package info.teksol.emulator.processor;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.ExpectedMessages;
import info.teksol.mindcode.compiler.SortCategory;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

// Some Mindcode code tested in this class can be quite complex and hard to maintain as a string constant.
// Such code can be saved as a file in the src/test/resources/scripts directory.
@Order(4)
public class ProcessorTest extends AbstractProcessorTest {

    public static final String SCRIPTS_DIRECTORY = "src/test/resources/info/teksol/emulator/processor/processor";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, ProcessorTest.class.getSimpleName());
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile()
                .setOptimizationLevel(Optimization.PRINT_TEXT_MERGING, OptimizationLevel.NONE);
    }

    @Test
    void testSuiteRecognizesUnexpectedWarnings() {
        TestCompiler testCompiler = createTestCompiler();
        testAndEvaluateCode(
                testCompiler,
                null,
                """
                        a = 10;
                        print("hi");
                        """,
                Map.of(),
                ExpectedMessages.create().add("List of unused variables: a."),
                createEvaluator(testCompiler, List.of("hi")),
                null
        );
    }

    @Test
    void printsItemIds() {
        testCode("""
                        param p = @coal;
                        print(p.id);
                        """,
                "5"
        );
    }

    @Test
    void printsNumbersStringsObjects() {
        testCode("""
                        print("Hello", 10, 5.5, bank1, bank1.type, null, true, false);
                        """,
                "Hello", "10", "5.5", "bank", "memory-bank", "null", "1", "0"
        );
    }

    @Test
    void printsInvalidValueAsNull() {
        testCode("""
                        param a = 1;
                        param b = 0;
                        print(a / b);
                        """,
                "null"
        );
    }


    @Test
    void computesPackColor() {
        testCode("""
                        c = packcolor(1.0, 1.0, 1.0, 1.0);
                        print(c);
                        """,
                "0"
        );
    }

    @Test
    void handlesChainedAssignments() {
        testCode("""
                        a = b = @counter;
                        print(a == b);
                        """,
                "1"
        );
    }

    @Test
    void handlesAssignmentsInConditions() {
        testCode("""
                        param a = 2;
                        param b = 3;
                        if res = a < b then
                            print(true);
                        end;
                        print(res);
                        """,
                "1", "1"
        );
    }

    @Test
    void expressionEvaluationRuntime() throws IOException {
        executeMindcodeUnitTests(createTestCompiler(
                        createCompilerProfile().setOptimizationLevel(Optimization.FUNCTION_INLINING, OptimizationLevel.NONE)),
                "expression-evaluation-runtime.mnd");
    }

    @Test
    void expressionEvaluationCompileTime() throws IOException {
        executeMindcodeUnitTests(createTestCompiler(), "expression-evaluation-compile-time.mnd");
    }

    void executeMindcodeUnitTests(TestCompiler compiler, String fileName) throws IOException {
        testAndEvaluateFile(compiler,
                fileName,
                s -> s,
                Map.of(),
                (useAsserts, actualOutput) -> {
                    String messages = actualOutput.stream().filter(s -> !"ok".equals(s)).collect(Collectors.joining());

                    if (useAsserts) {
                        assertFalse(actualOutput.isEmpty(), "Test didn't produce any output values.");
                        if (!messages.isEmpty()) {
                            fail("Some of the tests have failed:\n" + messages);
                        }
                    }
                    return !actualOutput.isEmpty() && messages.isEmpty();
                }
        );
    }

    @Test
    void supportsSortVariables() {
        TestCompiler testCompiler = createTestCompiler(createCompilerProfile()
                .setAllOptimizationLevels(OptimizationLevel.BASIC)
                .setSortVariables(List.of(SortCategory.ALL))
        );
        testAndEvaluateCode(testCompiler,
                null,
                """
                        #set sort-variables;
                        param p = 1;
                        step = p;
                        for out i in a, b, c, d do
                            i = step;
                            step *= 2;
                        end;
                        print(a, b, c, d);
                        """,
                Map.of(),
                ExpectedMessages.none(),
                createEvaluator(testCompiler, List.of("1", "2", "4", "8")),
                null
        );
    }

    @Test
    void compilesProperComparison() {
        testCode("""
                        inline def eval(b)
                            b ? "T" : "F";
                        end;
                        
                        inline def compare(n, a, b)
                            print(n, eval(a == b), eval(a != b), eval(a === b), eval(a !== b));
                        end;
                        
                        compare( 1, null, 0);
                        compare( 2, null, 1);
                        compare( 3, null, 2);
                        compare( 4, @coal, 0);
                        compare( 5, @coal, 1);
                        compare( 6, @coal, 2);
                        compare( 7, @coal, @lead);
                        compare( 8, "A", 0);
                        compare( 9, "A", 1);
                        compare(10, "A", 2);
                        compare(11, "A", "B");
                        compare(12, "A", "A");
                        """,
                "1", "T", "F", "F", "T",
                "2", "F", "T", "F", "T",
                "3", "F", "T", "F", "T",
                "4", "F", "T", "F", "T",
                "5", "T", "F", "F", "T",
                "6", "F", "T", "F", "T",
                "7", "F", "T", "F", "T",
                "8", "F", "T", "F", "T",
                "9", "T", "F", "F", "T",
                "10", "F", "T", "F", "T",
                "11", "F", "T", "F", "T",
                "12", "T", "F", "T", "F"
        );
    }

    @Test
    void compilesProperComparison2() {
        testCode("""
                        inline def eval(b)
                            b ? "T" : "F";
                        end;
                        
                        inline def compare(a, b)
                            print(eval(a >= b), eval(a <= b), eval(a > b), eval(a < b));
                        end;
                        
                        compare(A, A);
                        """,
                "T", "T", "F", "F"
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

    @Test
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
    void executesFunctionInlining() throws IOException {
        testAndEvaluateFile("function-inlining.mnd",
                List.of("550", "1050", "1550", "2050", "600", "1100", "1600", "2100", "650", "1150", "1650", "2150", "700", "1200", "1700", "2200"));
    }

    @Test
    void executesComplexCaseExpressions() throws IOException {
        testAndEvaluateFile("complex-case-expression.mnd",
                List.of("A", "B", "B", "B", "C", "C", "C", "D"));
    }

    @Test
    void executesSwitchedCaseExpressions() throws IOException {
        testAndEvaluateFile("switched-case-expression.mnd",
                List.of("11", "21", "20", "20", "20", "20", "20", "20", "20", "20"));
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
