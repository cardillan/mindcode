package info.teksol.mindcode.exttest.threadpool;

import info.teksol.mindcode.exttest.TestProgress;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import info.teksol.mindcode.exttest.cases.TestCaseExecutor;

import java.util.concurrent.Callable;

public class ThreadPoolRunner implements Callable<Integer> {
    private final TestProgress progress;
    private final TestCaseCreator caseCreator;
    private final TestCaseExecutor caseExecutor;

    public ThreadPoolRunner(TestProgress progress, TestCaseCreator caseCreator, TestCaseExecutor caseExecutor) {
        this.progress = progress;
        this.caseCreator = caseCreator;
        this.caseExecutor = caseExecutor;
    }

    @Override
    public Integer call() {
        int count = 0;
        while (!progress.finished()) {
            int testRunNumber = progress.nextTestRun();
            if (testRunNumber >= caseCreator.getSampleCount()) {
                break;
            }
            caseExecutor.runTest(caseCreator, testRunNumber);
            count++;
        }
        return count;
    }
}
