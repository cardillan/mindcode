package info.teksol.mindcode.exttest.forkjoin;

import info.teksol.mindcode.exttest.TestProgress;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import info.teksol.mindcode.exttest.cases.TestCaseExecutor;

import java.util.concurrent.RecursiveTask;

public class ForkJoinTestRunner extends RecursiveTask<Integer> {
    private final TestProgress progress;
    private final TestCaseCreator caseCreator;
    private final TestCaseExecutor caseExecutor;
    private final int start;
    private final int end;

    public ForkJoinTestRunner(TestProgress progress, TestCaseCreator caseCreator, TestCaseExecutor caseExecutor, int start, int end) {
        this.progress = progress;
        this.caseCreator = caseCreator;
        this.caseExecutor = caseExecutor;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (start > end) {
            return 0;
        } else if (start == end) {
            caseExecutor.runTest(caseCreator, start);
            return 1;
        } else {
            int middle = (start + end) / 2;
            ForkJoinTestRunner fork1 = new ForkJoinTestRunner(progress, caseCreator, caseExecutor, start, middle);
            ForkJoinTestRunner fork2 = new ForkJoinTestRunner(progress, caseCreator, caseExecutor, middle + 1, end);

            fork2.fork();
            return fork1.compute() + fork2.join();
        }
    }
}
