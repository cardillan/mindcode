package info.teksol.mindcode.exttest;

class TestRunnerTest {

    private static final int TEST_NUMBER = 2500;

//    //@Test
//    void testCase() {
//        TestCaseExecutor testCaseExecutor = new TestCaseExecutor();
//        testCaseExecutor.runTest(TEST_NUMBER);
//        assertEquals(0, testCaseExecutor.errorCount.get(), "Test failed.");
//    }
//
//    @Test
//    void printCompilerProfile() {
//        TestCaseExecutor testCaseExecutor = new TestCaseExecutor();
//        CompilerProfile profile = testCaseExecutor.createCompilerProfile(TEST_NUMBER);
//        System.out.println(profile.getOptimizationLevels().entrySet().stream()
//                .sorted(Comparator.comparing(e -> e.getKey().getOptionName()))
//                .map(e -> e.getKey().getOptionName() + " = " + e.getValue().name().toLowerCase())
//                .collect(Collectors.joining(";\n#set ", "Active optimizations:\n#set ", ";\n")));
//    }
}