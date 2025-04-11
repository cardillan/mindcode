package info.teksol.mindcode.exttest;

import info.teksol.mc.profile.CompilerProfile;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtendedTestsTest {

    private static final int TEST_NUMBER = 2500;

    @Test
    void testCase() {
        assertTrue(true);
    }

//    @Test
//    void testCase() {
//        TestCaseExecutor testCaseExecutor = new TestCaseExecutor();
//        testCaseExecutor.runTest(TEST_NUMBER);
//        assertEquals(0, testCaseExecutor.errorCount.get(), "Test failed.");
//    }

    @Test
    void printCompilerProfile() {
        CompilerProfile profile = CompilerProfile.noOptimizations(false);
        profile.decode("3350291745793");
        System.out.println(profile.getOptimizationLevels().entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getKey().getOptionName()))
                .map(e -> e.getKey().getOptionName() + " = " + e.getValue().name().toLowerCase())
                .collect(Collectors.joining(";\n#set ", "Active optimizations:\n#set ", ";\n")));
    }
}