package info.teksol.mindcode.exttest.threadpool;

import info.teksol.mindcode.exttest.TestProgress;
import info.teksol.mindcode.exttest.cases.TestCaseExecutor;
import info.teksol.mindcode.exttest.cases.TestCaseSelector;

import java.util.concurrent.Callable;

public class ThreadPoolRunner implements Callable<Integer> {
    private final TestProgress progress;
    private final TestCaseSelector caseSelector;
    private final TestCaseExecutor caseExecutor;

    public ThreadPoolRunner(TestProgress progress, TestCaseSelector caseSelector, TestCaseExecutor caseExecutor) {
        this.progress = progress;
        this.caseSelector = caseSelector;
        this.caseExecutor = caseExecutor;
    }

    @Override
    public Integer call() {
        int count = 0;
        while (true) {
            int testRunNumber = progress.nextSample.getAndIncrement();
            if (testRunNumber >= caseSelector.getSampleCount()) {
                break;
            }
            caseExecutor.runTest(caseSelector.getTestCaseNumber(testRunNumber));
            count++;
        }
        return count;
    }

    public static Callable<Integer> create(TestProgress progress, TestCaseSelector caseSelector, TestCaseExecutor testCaseExecutor) {
        return new ThreadPoolRunner(progress, caseSelector, testCaseExecutor);
    }
}
