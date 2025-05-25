package info.teksol.mindcode.exttest.threadpool;

import info.teksol.mindcode.exttest.TestProgress;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import info.teksol.mindcode.exttest.cases.TestCaseExecutor;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.Callable;

@NullMarked
public class ThreadPoolRunner implements Callable<Integer> {
    private final TestProgress progress;
    private final TestCaseCreator caseCreator;

    public ThreadPoolRunner(TestProgress progress, TestCaseCreator caseCreator) {
        this.progress = progress;
        this.caseCreator = caseCreator;
    }

    @Override
    public Integer call() {
        int count = 0;
        while (!progress.finished()) {
            int testRunNumber = progress.nextTestRun();
            if (testRunNumber >= caseCreator.getSampleCount()) {
                break;
            }
            TestCaseExecutor executor = caseCreator.createExecutor(testRunNumber);
            executor.runTest(progress);
            count++;
        }
        return count;
    }
}
