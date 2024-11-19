package info.teksol.mindcode.exttest;

import info.teksol.mindcode.exttest.cases.TestCaseExecutor;
import info.teksol.mindcode.exttest.cases.TestCaseSelector;

import java.util.concurrent.RecursiveTask;

public class TestRunner extends RecursiveTask<Integer> {
    private static final int BATCH_SIZE = 25;

    private final TestCaseSelector testCaseSelector;
    private final TestCaseExecutor testCaseExecutor;
    private final int start;
    private final int end;

    public TestRunner(TestCaseSelector testCaseSelector, TestCaseExecutor testCaseExecutor, int start, int end) {
        this.testCaseSelector = testCaseSelector;
        this.testCaseExecutor = testCaseExecutor;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (end - start <= BATCH_SIZE) {
            for (int testRunNumber = start; testRunNumber < end; testRunNumber++) {
                testCaseExecutor.runTest(testCaseSelector.getTestCaseNumber(testRunNumber));
            }
            return end - start;
        } else {
            int middle = (start + end) / 2;
            TestRunner fork1 = new TestRunner(testCaseSelector, testCaseExecutor, start, middle);
            TestRunner fork2 = new TestRunner(testCaseSelector, testCaseExecutor, middle, end);

            fork2.fork();
            return fork1.compute() + fork2.join();
        }
    }

}
